let currentUser = null;

let tasks = [];
let filteredTasks = [];
let currentPage = 1;
let editingTaskId = null;

const ITEMS_PER_PAGE = 6;

/* ================= CRIAR / EDITAR ================= */
function createTask() {

    if (!currentUser) {
        alert("Você precisa selecionar um usuário antes de criar tarefas.");
        return;
    }

    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();
    const status = document.getElementById("status").value;

    if (!title) return;

    if (editingTaskId !== null) {
        const task = tasks.find(t => t.id === editingTaskId);
        if (!task) return;

        task.title = title;
        task.description = description;
        task.status = status;

        editingTaskId = null;
        document.getElementById("submitBtn").textContent = "Criar";
    } else {
        tasks.unshift({
            id: Date.now(),
            title,
            description,
            status,
            user: currentUser.name
        });
    }

    limparFormulario();
    aplicarFiltros();
}

/* ================= LIMPAR FORM ================= */
function limparFormulario() {
    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
    document.getElementById("status").value = "PENDING";
}

/* ================= FILTROS ================= */
function aplicarFiltros() {
    const searchInput = document.getElementById("searchInput");
    const filterStatusSelect = document.getElementById("filterStatus");

    const search = searchInput ? searchInput.value.toLowerCase() : "";
    const statusFilter = filterStatusSelect ? filterStatusSelect.value : "ALL";

    filteredTasks = tasks.filter(task => {
        const texto = `${task.title} ${task.description}`.toLowerCase();
        const matchTexto = texto.includes(search);
        const matchStatus =
            statusFilter === "ALL" || task.status === statusFilter;

        return matchTexto && matchStatus;
    });

    const totalPages = Math.ceil(filteredTasks.length / ITEMS_PER_PAGE);

    if (currentPage > totalPages) currentPage = totalPages;
    if (currentPage < 1) currentPage = 1;

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
    if (!list) return;

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
                <p>${task.description}</p>
                <small class="task-user">
                    Criado por: ${task.user || "—"}
                </small>
            </div>

            <div class="status-badge ${getStatusClass(task.status)}">
                ${getStatusLabel(task.status)}
            </div>

            <div class="task-actions">
                <button title="Editar" onclick="editTask(${task.id})">✏️</button>
                <button title="Excluir" onclick="deleteTask(${task.id})">❌</button>
            </div>
        `;

        list.appendChild(li);
    });
}

/* ================= EDIT ================= */
function editTask(id) {
    const task = tasks.find(t => t.id === id);
    if (!task) return;

    document.getElementById("title").value = task.title;
    document.getElementById("description").value = task.description;
    document.getElementById("status").value = task.status;

    editingTaskId = id;
    document.getElementById("submitBtn").textContent = "Salvar";
}

/* ================= DELETE ================= */
function deleteTask(id) {
    const confirmacao = confirm("Deseja excluir esta tarefa?");
    if (!confirmacao) return;

    tasks = tasks.filter(task => task.id !== id);
    aplicarFiltros();
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

        const prev = document.createElement("button");
        prev.textContent = "‹";
        prev.disabled = currentPage === 1;
        prev.onclick = () => {
            currentPage--;
            render();
        };
        container.appendChild(prev);

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

        const next = document.createElement("button");
        next.textContent = "›";
        next.disabled = currentPage === totalPages;
        next.onclick = () => {
            currentPage++;
            render();
        };
        container.appendChild(next);
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

/* ================= USER ================= */

function toggleUserBox() {
    const box = document.getElementById("userDropdown");
    if (!box) return;
    box.classList.toggle("active");
}

function handleUserSelection() {
    const input = document.getElementById("userNameInput");
    if (!input) return;

    const name = input.value.trim();
    if (!name) {
        alert("Digite o nome do usuário.");
        return;
    }

    currentUser = { name };

    updateUserIcon();
    document.getElementById("userDropdown").classList.remove("active");

    input.value = "";

    alert("Usuário selecionado: " + currentUser.name);
}

function updateUserIcon() {
    const icon = document.getElementById("userIcon");
    const nameDisplay = document.getElementById("loggedUserName");

    if (!icon || !nameDisplay) return;

    if (!currentUser) {
        icon.innerText = "👤";
        nameDisplay.innerText = "";
        return;
    }

    const names = currentUser.name.split(" ");
    const initials =
        names.length > 1
            ? names[0][0] + names[1][0]
            : names[0][0];

    icon.innerText = initials.toUpperCase();
    nameDisplay.innerText = currentUser.name + " (usuário logado)";
}

/* ===== FECHAR DROPDOWN CORRETAMENTE ===== */
document.addEventListener("click", function (event) {
    const container = document.querySelector(".user-container");
    const dropdown = document.getElementById("userDropdown");

    if (!container || !dropdown) return;

    if (!container.contains(event.target)) {
        dropdown.classList.remove("active");
    }
});

/* Impede fechar ao clicar dentro */
document.addEventListener("DOMContentLoaded", function () {
    const dropdown = document.getElementById("userDropdown");
    if (dropdown) {
        dropdown.addEventListener("click", function (event) {
            event.stopPropagation();
        });
    }

    aplicarFiltros();
});