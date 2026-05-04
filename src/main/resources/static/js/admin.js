// ==========================
// CARREGAR USUÁRIOS
// ==========================
async function loadUsers() {
    const response = await fetch("/admin/users");
    const data = await response.json();

    const tbody = document.querySelector("#usersTable tbody");
    tbody.innerHTML = "";

    data.content.forEach(user => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${user.id}</td>
            <td>${user.name}</td>
            <td>${user.login}</td>
            <td>${user.role}</td>
            <td>${user.active}</td>
            <td>
                <button onclick="makeAdmin(${user.id})" class="btn btn-sm btn-warning">Admin</button>
                <button onclick="makeUser(${user.id})" class="btn btn-sm btn-secondary">User</button>
                <button onclick="deactivate(${user.id})" class="btn btn-sm btn-danger">Desativar</button>
            </td>
        `;

        tbody.appendChild(tr);
    });
}

// ==========================
// CARREGAR TAREFAS
// ==========================
async function loadTasks() {
    const response = await fetch("/admin/tasks");
    const data = await response.json();

    const tbody = document.querySelector("#tasksTable tbody");
    tbody.innerHTML = "";

    data.content.forEach(task => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${task.id}</td>
            <td>${task.title}</td>
            <td>${task.status}</td>
            <td>${task.user?.name || "-"}</td>
        `;

        tbody.appendChild(tr);
    });
}

// ==========================
// AÇÕES
// ==========================
async function makeAdmin(id) {
    await fetch(`/admin/users/${id}/role?role=ADMIN`, { method: "PATCH" });
    loadUsers();
}

async function makeUser(id) {
    await fetch(`/admin/users/${id}/role?role=USER`, { method: "PATCH" });
    loadUsers();
}

async function deactivate(id) {
    await fetch(`/admin/users/${id}/deactivate`, { method: "PATCH" });
    loadUsers();
}

// ==========================
// INIT
// ==========================
loadUsers();
loadTasks();