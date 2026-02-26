// ================= CONFIGURAÇÃO =================

// URL base da API (Spring Boot)
const API_URL = "http://localhost:8080/tasks";

// Tradução dos status (backend → frontend)
const STATUS_PT = {
    PENDING: "Pendente",
    IN_PROGRESS: "Em andamento",
    COMPLETED: "Concluída"
};

// Classe CSS associada a cada status
const STATUS_CLASS = {
    PENDING: "status-pending",
    IN_PROGRESS: "status-in-progress",
    COMPLETED: "status-completed"
};

// Controle de edição
// null = criar | id = editar
let editingTaskId = null;

// Lista global para filtros
let todasAsTarefas = [];


// ================= LISTAR TAREFAS =================

function carregarTarefas() {

    fetch(API_URL)
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao buscar tarefas");
            }
            return response.json();
        })
        .then(data => {

            if (!data || !Array.isArray(data.content)) {
                renderizarTarefas([]);
                return;
            }

            todasAsTarefas = data.content;
            renderizarTarefas(todasAsTarefas);
        })
        .catch(error => {
            console.error(error);
            document.getElementById("taskList").innerHTML =
                "<li>Erro ao carregar tarefas</li>";
        });
}


// ================= FILTRO + BUSCA INTELIGENTE =================

function aplicarFiltros() {

    const termo = document
        .getElementById("searchInput")
        .value
        .toLowerCase()
        .trim();

    const termoNumerico = termo.replace(/\D/g, "");

    const statusSelecionado =
        document.getElementById("filterStatus").value;

    const filtradas = todasAsTarefas.filter(task => {

        const textoCompleto = `
            ${task.title}
            ${task.description}
        `.toLowerCase();

        const textoNumerico = textoCompleto.replace(/\D/g, "");

        const bateTexto =
            textoCompleto.includes(termo);

        const bateCpf =
            termoNumerico &&
            textoNumerico.includes(termoNumerico);

        const bateStatus =
            statusSelecionado === "ALL" ||
            task.status === statusSelecionado;

        return (bateTexto || bateCpf) && bateStatus;
    });

    renderizarTarefas(filtradas);
}


// ================= RENDERIZAÇÃO =================

function renderizarTarefas(tasks) {

    const lista = document.getElementById("taskList");
    lista.innerHTML = "";

    if (!tasks || tasks.length === 0) {
        lista.innerHTML = "<li>Nenhuma tarefa encontrada</li>";
        return;
    }

    tasks.forEach(task => {

        const li = document.createElement("li");
        li.className = "task-card";

        li.innerHTML = `
            <div class="task-info">
                <strong>${task.title}</strong>
                <p>${task.description}</p>
            </div>

            <span class="status-badge ${STATUS_CLASS[task.status]}">
                ${STATUS_PT[task.status]}
            </span>

            <div class="task-actions">
                <button onclick='editTask(${JSON.stringify(task)})'>✏️</button>
                <button onclick='deleteTask(${task.id})'>❌</button>
            </div>
        `;

        lista.appendChild(li);
    });
}


// ================= CRIAR OU ATUALIZAR =================

function createTask() {

    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();
    const status = document.getElementById("status").value;

    if (!title || !description) {
        alert("Preencha todos os campos");
        return;
    }

    const task = {
        title,
        description,
        status,
        userId: 1
    };

    // ================= UPDATE =================
    if (editingTaskId !== null) {

        fetch(`${API_URL}/${editingTaskId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(task)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Erro ao atualizar tarefa");
                }
                return response.json();
            })
            .then(() => {
                resetForm();
                carregarTarefas();
            })
            .catch(error => {
                console.error(error);
                alert("Erro ao atualizar tarefa");
            });

        return;
    }

    // ================= CREATE =================
    fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(task)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao criar tarefa");
            }
            return response.json();
        })
        .then(() => {
            resetForm();
            carregarTarefas();
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao criar tarefa");
        });
}


// ================= EDITAR =================

function editTask(task) {

    document.getElementById("title").value = task.title;
    document.getElementById("description").value = task.description;
    document.getElementById("status").value = task.status;

    editingTaskId = task.id;
    document.getElementById("submitBtn").textContent = "Atualizar";
}


// ================= EXCLUIR =================

function deleteTask(id) {

    if (!confirm("Tem certeza que deseja excluir esta tarefa?")) {
        return;
    }

    fetch(`${API_URL}/${id}`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao excluir tarefa");
            }
            carregarTarefas();
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao excluir tarefa");
        });
}


// ================= RESET =================

function resetForm() {

    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
    document.getElementById("status").value = "PENDING";

    editingTaskId = null;
    document.getElementById("submitBtn").textContent = "Criar";
}


// ================= AUTO LOAD =================

document.addEventListener("DOMContentLoaded", carregarTarefas);