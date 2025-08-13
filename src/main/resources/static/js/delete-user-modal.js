var userIdToDelete;

document.getElementById('deleteConfirmModal').addEventListener('show.bs.modal', function(event) {
    const button = event.relatedTarget;
    userIdToDelete = button.getAttribute('data-user-id');
});


document.getElementById('confirmDelete').addEventListener('click', async function() {
    if (!userIdToDelete) return;
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    try {
        const response = await fetch(`/api/users/${userIdToDelete}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer '+ sessionStorage.getItem("jwt")
            }
        });

        if (response.ok) {
            window.location.reload(); // Refresh the page
        } else {
            alert("Failed to delete user.");
        }
    } catch (error) {
        console.error("Error:", error);
    }

    // Close the modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal'));
    modal.hide();
});