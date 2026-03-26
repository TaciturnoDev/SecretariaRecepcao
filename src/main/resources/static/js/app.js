let loggedUser = null;

let tasks = [];
let filteredTasks = [];
let currentPage = 1;
let editingTaskId = null;

const ITEMS_PER_PAGE = 6;

/* ================= NORMALIZAR CPF ================= */
function normalizeCPF(cpf) {
    if (!cpf) return "";
    return cpf.replace(/\D/g, "");
}

/* ================= CRIAR ================= */
async function createTask() {

    if (!loggedUser) {
        alert("Usuário não autenticado");
        return;
    }

    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();

    if (!title) return;

    try {
        const response = await fetch("/tasks", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                title,
                description
            })
        });

        if (response.ok) {
            await carregarTarefas();
            limparFormulario();
        } else {
            alert("Erro ao salvar tarefa");
        }

    } catch (error) {
        console.error("Erro:", error);
    }
}

/* ================= LIMPAR ================= */
function limparFormulario() {
    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
}

/* ================= FILTROS ================= */
function aplicarFiltros() {

    const searchRaw = document.getElementById("searchInput").value.toLowerCase().trim();
    const searchCPF = normalizeCPF(searchRaw);

    const statusFilter = document.getElementById("filterStatus").value;
    const userFilter = document.getElementById("filterUser").value;

    filteredTasks = tasks.filter(task => {

        const matchUser =
            userFilter === "ALL" || task.userName === userFilter;

        const matchStatus =
            statusFilter === "ALL" || task.status === statusFilter;

        const title = task.title?.toLowerCase() || "";
        const description = task.description?.toLowerCase() || "";

        const taskCPF = normalizeCPF(task.cpf);

        const matchSearch =
            !searchRaw ||
            title.includes(searchRaw) ||
            description.includes(searchRaw) ||
            taskCPF.includes(searchCPF);

        return matchUser && matchStatus && matchSearch;
    });

    render();
}

/* ================= RENDER ================= */
function render() {
    renderTasks();
    renderPagination();
}

/* ================= TAREFAS ================= */
function renderTasks() {

    const list = document.getElementById("taskList");
    list.innerHTML = "";

    if (filteredTasks.length === 0) {
        list.innerHTML = "<p style='text-align:center;'>Nenhuma tarefa encontrada.</p>";
        return;
    }

    const start = (currentPage - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;

    filteredTasks.slice(start, end).forEach(task => {

        const li = document.createElement("li");
        li.className = "task-card";

        li.innerHTML = `
            <div class="task-info">
                <strong>${task.title}</strong>
                <p>${task.description || ""}</p>

                <div class="task-meta">
                    <div class="task-user-badge">
                        👤 ${task.userName?.toUpperCase() || "—"}
                    </div>

                    <div class="task-date">
                        📅 ${task.createdAt ? formatDate(task.createdAt) : ""}
                    </div>
                </div>
            </div>

            <div class="status-badge ${getStatusClass(task.status)}">
                ${getStatusLabel(task.status)}
            </div>

            <div class="task-actions">
                <button onclick="deleteTask(${task.id})">❌</button>
            </div>
        `;

        list.appendChild(li);
    });
}

/* ================= DELETE ================= */
async function deleteTask(id) {

    if (!confirm("Deseja excluir esta tarefa?")) return;

    try {
        const response = await fetch(`/tasks/${id}`, {
            method: "DELETE"
        });

        if (response.ok) {
            await carregarTarefas();
        } else {
            alert("Erro ao excluir");
        }

    } catch (e) {
        console.error("Erro ao deletar:", e);
    }
}

/* ================= PAGINAÇÃO ================= */
function renderPagination() {

    const totalPages = Math.ceil(filteredTasks.length / ITEMS_PER_PAGE);

    const top = document.getElementById("paginationTop");
    const bottom = document.getElementById("paginationBottom");

    [top, bottom].forEach(container => {

        if (!container) return;

        container.innerHTML = "";

        if (totalPages <= 1) return;

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement("button");
            btn.textContent = i;

            if (i === currentPage) btn.classList.add("active");

            btn.onclick = () => {
                currentPage = i;
                render();
            };

            container.appendChild(btn);
        }
    });
}

/* ================= STATUS ================= */
function getStatusClass(status) {
    if (status === "PENDING") return "status-pending";
    if (status === "IN_PROGRESS") return "status-in-progress";
    return "status-completed";
}

function getStatusLabel(status) {
    if (status === "PENDING") return "Pendente";
    if (status === "IN_PROGRESS") return "Em andamento";
    return "Concluída";
}

/* ================= DATA ================= */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString("pt-BR");
}

/* ================= USER FILTER ================= */
function updateUserFilterOptions() {

    const select = document.getElementById("filterUser");

    const users = [...new Set(tasks.map(t => t.userName))];

    select.innerHTML = `<option value="ALL">Todos</option>`;

    users.forEach(user => {
        if (!user) return;

        const option = document.createElement("option");
        option.value = user;
        option.textContent = user;
        select.appendChild(option);
    });
}

/* ================= SETORES ================= */
async function carregarSetores() {

    const response = await fetch("/sectors/with-users");
    const setores = await response.json();

    const lista = document.getElementById("lista-setores");
    lista.innerHTML = "";

    setores.forEach(setor => {

		const li = document.createElement("li");
		li.classList.add("setor-container");

		li.innerHTML = `
		    <div class="setor-header" onclick="toggleSetor(this)">
		        <span class="icon">📁</span>
		        <span class="text">${setor.name}</span>
		        <span class="arrow">▶</span>
		    </div>
		    <ul class="setor-users"></ul>
		`;

        const ul = li.querySelector(".setor-users");

        setor.users.forEach(user => {
            const userLi = document.createElement("li");
            userLi.innerText = "👤 " + user.name;
            ul.appendChild(userLi);
        });

        lista.appendChild(li);
    });
}

function toggleSetor(el) {
    const container = el.parentElement;
    container.classList.toggle("open");
}

/* ================= USUÁRIO LOGADO ================= */
async function carregarUsuarioLogado() {
    try {
        const response = await fetch("/auth/me");

        const data = await response.json();

        loggedUser = data.username;

        document.getElementById("loggedUserName").innerText = data.username;

    } catch (e) {
        console.error("Erro ao pegar usuário:", e);
    }
}

/* ================= CARREGAR TAREFAS ================= */
async function carregarTarefas() {

    const response = await fetch("/tasks?page=0&size=50");

    const data = await response.json();

    tasks = data.content;

    updateUserFilterOptions();
    aplicarFiltros();
}

/* ================= INIT ================= */
document.addEventListener("DOMContentLoaded", () => {

    carregarUsuarioLogado();
    carregarTarefas();
    carregarSetores();

    document.getElementById("filterUser")
        .addEventListener("change", aplicarFiltros);
});

/* ======= EXPANDIR FUNÇÃO===== */
function toggleSidebar() {
    const sidebar = document.querySelector(".sidebar");
    sidebar.classList.toggle("open");
}

/* ================= CRIAR SETOR ================= */
async function abrirCriarSetor() {

    const nome = prompt("Nome do setor:");

    if (!nome || nome.trim() === "") {
        alert("Nome inválido");
        return;
    }

    try {
        const response = await fetch("/sectors", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: nome
            })
        });

        if (response.ok) {
            await carregarSetores(); // 🔥 recarrega lista
        } else {
            alert("Erro ao criar setor");
        }

    } catch (e) {
        console.error("Erro:", e);
    }
}

/* FILTROS SETORES */ 

function filtrarSetores() {

    const termo = document
        .getElementById("searchSetor")
        .value.toLowerCase();

    const setores = document.querySelectorAll(".setor-container");

    setores.forEach(setor => {

        const nome = setor
            .querySelector(".text")
            .innerText
            .toLowerCase();

        if (nome.includes(termo)) {
            setor.style.display = "block";
        } else {
            setor.style.display = "none";
        }
    });
}

/* ======== DASHBOARDS ======== */ 

function atualizarDashboard() {

    document.getElementById("totalTasks").innerText = tasks.length;

    document.getElementById("pendingTasks").innerText =
        tasks.filter(t => t.status === "PENDING").length;

    document.getElementById("progressTasks").innerText =
        tasks.filter(t => t.status === "IN_PROGRESS").length;

    document.getElementById("doneTasks").innerText =
        tasks.filter(t => t.status === "COMPLETED").length;
}