document.getElementById('addUserForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const formData = {
        firstName: document.querySelector('[name="firstName"]').value,
        lastName: document.querySelector('[name="lastName"]').value,
        email: document.querySelector('[name="email"]').value,
        password: document.querySelector('[name="password"]').value,
        confirmationPassword: document.querySelector('[name="confirmationPassword"]').value,
    }
    try {
        const response = await fetch('/api/register',{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
            },
            body: JSON.stringify(formData)
        });

        if(response.ok){
            window.location.reload();
        } else {
            const errorData = await response.json();
            console.log("errorData",errorData);
            displayErrors(errorData);
        }

    } catch (error){
        console.error("Error:" + error)
    }

  });

function displayErrors(errors){
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.textContent = '');

    for(const [field, message] of Object.entries(errors)){
        console.log(field)
        const input = document.querySelector(`[name="${field}"]`);
        const errorElement = document.getElementById(`${field}Error`);

        if(errorElement){
            input.classList.add('is-invalid');
            errorElement.textContent = message;
            console.log(errorElement);
        }
    }

}