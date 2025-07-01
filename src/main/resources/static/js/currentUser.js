document.addEventListener('DOMContentLoaded', function() {

    const showPasswordBtn = document.getElementById('showPasswordChangeBtn');
    const cancelPasswordBtn = document.getElementById('cancelPasswordChangeBtn');
    const passwordSection = document.getElementById('passwordChangeSection');
    const profileForm = document.getElementById('profileForm');

    const showPasswordSection = passwordSection.getAttribute('data-show-password');


    const emailInput = document.getElementById('email');

    emailInput.addEventListener('input', function() {
        this.classList.remove('is-invalid');
    });

    showPasswordBtn.addEventListener('click',function(){
        passwordSection.style.display = 'block';
        profileForm.style.opacity = '0.5';
        showPasswordBtn.style.display = 'none';
    });

    cancelPasswordBtn.addEventListener('click', function() {
        passwordSection.style.display = 'none';
        profileForm.style.opacity = '1';
        showPasswordBtn.style.display = 'inline-block';
    });


    if(showPasswordSection == "true" || showPasswordSection == true){

        passwordSection.style.display = 'block';
        profileForm.style.opacity = '0.5';
        showPasswordBtn.style.display = 'none';
    }
});
