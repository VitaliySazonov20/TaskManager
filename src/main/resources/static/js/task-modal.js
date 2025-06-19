document.querySelector('.add-task-btn').addEventListener('click', async () => {
  try{
    const response = await fetch('/api/users');
    if(!response.ok){
        throw new Error('Failed to fetch users')
    }
    const users = await response.json();
    const select = document.getElementById('assignedTo');

    users.forEach(user =>{
        const option = document.createElement('option');
        option.value = user.id;
        option.textContent = user.firstName + " "+ user.lastName;
        select.appendChild(option);

    });
  } catch (error){
    console.error('Error: loading users:', error);
  }
});