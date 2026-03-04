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
            user: currentUser.name,
            createdAt: new Date().toISOString()
        });
    }

    limparFormulario();
    updateUserFilterOptions();
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

    const filterUserSelect = document.getElementById("filterUser");
    const userFilter = filterUserSelect ? filterUserSelect.value : "ALL";

    filteredTasks = tasks.filter(task => {
        const matchUser =
            userFilter === "ALL" || task.user === userFilter;

        return matchUser;
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

                <div class="task-meta">
                    <div class="task-user-badge">
                        👤 ${task.user ? task.user.toUpperCase() : "—"}
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
                <button onclick="editTask(${task.id})">✏️</button>
                <button onclick="deleteTask(${task.id})">❌</button>
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
    if (!confirm("Deseja excluir esta tarefa?")) return;

    tasks = tasks.filter(task => task.id !== id);

    updateUserFilterOptions();
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

/* ================= USER ================= */
function initUserSystem() {

    const userIcon = document.getElementById("userIcon");
    const dropdown = document.getElementById("userDropdown");
    const enterBtn = document.getElementById("enterUserBtn");

    if (!userIcon || !dropdown || !enterBtn) return;

    userIcon.addEventListener("click", function (event) {
        event.stopPropagation();
        dropdown.classList.toggle("active");
    });

    enterBtn.addEventListener("click", function () {

        const input = document.getElementById("userNameInput");
        const name = input.value.trim();

        if (!name) {
            alert("Digite o nome do usuário.");
            return;
        }

        currentUser = { name };

        updateUserDisplay();
        dropdown.classList.remove("active");
        input.value = "";
    });
}

function updateUserDisplay() {

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
    nameDisplay.innerText = currentUser.name;
}

/* ================= DATA ================= */
function formatDate(dateString) {

    const date = new Date(dateString);
    const today = new Date();

    const isToday =
        date.getDate() === today.getDate() &&
        date.getMonth() === today.getMonth() &&
        date.getFullYear() === today.getFullYear();

    const formatted = date.toLocaleDateString("pt-BR");

    return isToday
        ? `${formatted} (Criada hoje)`
        : formatted;
}

/* ================= FILTRO USER ================= */
function updateUserFilterOptions() {

    const select = document.getElementById("filterUser");
    if (!select) return;

    const users = [...new Set(tasks.map(t => t.user))];

    select.innerHTML = `<option value="ALL">Todos os usuários</option>`;

    users.forEach(user => {
        if (!user) return;
        const option = document.createElement("option");
        option.value = user;
        option.textContent = user;
        select.appendChild(option);
    });
}

/* ================= INIT ================= */
document.addEventListener("DOMContentLoaded", () => {

    initUserSystem();
    updateUserFilterOptions();
    aplicarFiltros();

    const filterUser = document.getElementById("filterUser");
    if (filterUser) {
        filterUser.addEventListener("change", () => {
            currentPage = 1;
            aplicarFiltros();
        });
    }

});