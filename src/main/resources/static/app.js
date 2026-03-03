let tasks = [];
let filteredTasks = [];
let currentPage = 1;
let editingTaskId = null;

const ITEMS_PER_PAGE = 6;

/* ================= CRIAR / EDITAR ================= */
function createTask() {
    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();
    const status = document.getElementById("status").value;

    if (!title) return;

    if (editingTaskId !== null) {
        const task = tasks.find(t => t.id === editingTaskId);
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
            status
        });
    }

    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
    document.getElementById("status").value = "PENDING";

    currentPage = 1;
    aplicarFiltros();
}

/* ================= FILTROS ================= */
function aplicarFiltros() {
    const search = document.getElementById("searchInput").value.toLowerCase();
    const statusFilter = document.getElementById("filterStatus").value;

    filteredTasks = tasks.filter(task => {
        const texto = `${task.title} ${task.description}`.toLowerCase();
        const matchTexto = texto.includes(search);
        const matchStatus =
            statusFilter === "ALL" || task.status === statusFilter;

        return matchTexto && matchStatus;
    });

    currentPage = 1;
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

    const start = (currentPage - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;

    filteredTasks.slice(start, end).forEach(task => {
        const li = document.createElement("li");
        li.className = "task-card";

        li.innerHTML = `
            <div class="task-info">
                <strong>${task.title}</strong>
                <p>${task.description}</p>
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

    if ((currentPage - 1) * ITEMS_PER_PAGE >= tasks.length) {
        currentPage = Math.max(1, currentPage - 1);
    }

    aplicarFiltros();
}

/* ================= PAGINAÇÃO ================= */
function renderPagination() {
    const totalPages = Math.ceil(filteredTasks.length / ITEMS_PER_PAGE);

    const top = document.getElementById("paginationTop");
    const bottom = document.getElementById("paginationBottom");

    [top, bottom].forEach(container => {
        container.innerHTML = "";
        if (totalPages <= 1) return;

        const prev = document.createElement("button");
        prev.textContent = "‹";
        prev.disabled = currentPage === 1;
        prev.onclick = () => { currentPage--; render(); };
        container.appendChild(prev);

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement("button");
            btn.textContent = i;
            if (i === currentPage) btn.classList.add("active");
            btn.onclick = () => { currentPage = i; render(); };
            container.appendChild(btn);
        }

        const next = document.createElement("button");
        next.textContent = "›";
        next.disabled = currentPage === totalPages;
        next.onclick = () => { currentPage++; render(); };
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

/* ================= INIT ================= */
aplicarFiltros();