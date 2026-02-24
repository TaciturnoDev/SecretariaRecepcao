const API_URL = "http://localhost:8080/tasks";

function carregarTarefas() {

    fetch(API_URL)
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao buscar tarefas");
            }
            return response.json();
        })
        .then(data => {

            const lista = document.getElementById("taskList");
            lista.innerHTML = "";

            if (!data || !Array.isArray(data.content)) {
                console.error("Resposta inválida da API:", data);
                lista.innerHTML = "<li>Erro ao carregar tarefas</li>";
                return;
            }

            const tasks = data.content;

            if (tasks.length === 0) {
                lista.innerHTML = "<li>Nenhuma tarefa encontrada</li>";
                return;
            }

            tasks.forEach(task => {
                const li = document.createElement("li");
                li.textContent =
                    `${task.title} - ${task.description} (${task.status})`;
                lista.appendChild(li);
            });
        })
        .catch(error => {
            console.error("Erro ao carregar tarefas:", error);
            document.getElementById("taskList").innerHTML =
                "<li>Erro ao carregar tarefas</li>";
        });
}

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
            document.getElementById("title").value = "";
            document.getElementById("description").value = "";
            carregarTarefas();
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao criar tarefa");
        });
}

document.addEventListener("DOMContentLoaded", carregarTarefas);