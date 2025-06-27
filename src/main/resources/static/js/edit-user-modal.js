var userIdToEdit;

document.getElementById('editUserModal').addEventListener('show.bs.modal', async function(event) {
    const button = event.relatedTarget;
    userIdToEdit = button.getAttribute('data-user-id');
    try {
        const response = await fetch(`/api/users/${userIdToEdit}`);
        if (!response.ok) throw new Error("Failed to fetch user");

        const user = await response.json();

        document.getElementById('userFirstName').value = user.firstName || '';
        document.getElementById('userLastName').value = user.lastName || '';
        document.getElementById('userEmail').value = user.email || '';
    } catch (error){
        console.error("Error fetching user: " ,  error);
        alert("Could not load user data");
    }
});

function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

document.getElementById('confirmChanges').addEventListener('click', async function() {
    if (!userIdToEdit) return;
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    const userData = {
        firstName: document.getElementById('userFirstName').value.trim(),
        lastName: document.getElementById('userLastName').value.trim(),
        email: document.getElementById('userEmail').value.trim()
    }
    if(!isValidEmail(userData['email'])){
        alert("Please enter a valid email address")
        return;
    }

    try {
        const response = await fetch(`/api/users/${userIdToEdit}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(userData)
        });

        if (response.ok) {
            window.location.reload(); // Refresh the page
        } else {
            alert("Failed to update user.");
        }
    } catch (error) {
        console.error("Error:", error);
    }

    // Close the modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
    modal.hide();
});