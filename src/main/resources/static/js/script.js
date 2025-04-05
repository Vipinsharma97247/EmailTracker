if (localStorage.getItem("username")) {
    document.querySelector("ul").innerHTML =
        `<li><a href="./index.html">Home</a></li>
                <li><a href="./email_send.html">Compose</a></li>
                <li><a href="./email_draft.html">Drafts</a></li>
                <li><a href="./email_sent_dashboard.html">Dashboard</a></li>
                <li><a href="#" class="logout">Logout</a></li>
                `;
document.querySelector(".cta-button").href="./email_send.html";
}
else{
    document.querySelector(".cta-button").href="./email_login.html";
}


{/* <li class="user-menu">
                    <a href="#">User</a>
                    <ul class="dropdown">
                        <li><a href="email_user_dashboard.html">User Dashboard</a></li>
                        <hr>
                        <li><a href="#" class="logout" style="color:rgb(253, 97, 97)">Logout</a></li>
                    </ul>
                </li> */}


document.querySelector(".logout").addEventListener("click",()=>{
    const popup = document.getElementById('logout-popup');


    popup.classList.add('show');
    setTimeout(() => {
       

        popup.classList.remove('show');
 
        window.location.href="./logout.html"
     }, 5000); 
})

