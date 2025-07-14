document.getElementById('exportLogsBtn').addEventListener('click', async function(){
    const btn = this;
    const spinner = document.getElementById('exportSpinner');

    btn.disabled=true;
    spinner.style.display = 'inline-block';

    try{
        const params = new URLSearchParams(window.location.search);

        params.delete('page');
        params.delete('size');

        window.location.href = `/api/logs/export?${params.toString()}`;
    } catch (error){
        console.error("Export failed: ", error);
        alert("Export failed. Please try again later.");
    } finally {
        setTimeout(()=>{
            btn.disabled = false;
            spinner.style.display = 'none';

        },2000);
    }
})