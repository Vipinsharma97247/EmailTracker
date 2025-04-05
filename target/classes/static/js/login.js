if (localStorage.getItem("username")) {
    document.querySelector("ul").innerHTML =
        `<li><a href="./index.html">Home</a></li>
                <li><a href="./email_sent.html">Compose</a></li>
                <li><a href="./email_draft.html">Drafts</a></li>
                <li><a href="./email_sent_dashboard">Dashboard</a></li>
                <li><a href="#" class="logout">Logout</a></li>
                `;

    window.location.href = "email_send.html";
}


let verificationCode = "";
let website_url = "https://emailtracker.up.railway.app";

if (localStorage.getItem("username")) {
    window.location.href = '/index.html';
}


let authenticate_with_code = async (code) => {

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    console.log(username)
    await fetch(website_url + "/authenticate", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            'username': username,
            'password': password,
            'to': username,
            'code': code,

        })
    })
        .then(response => response.json())
        .then(result => console.log(result));
}

function sendCode() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    if (username === "" || password === "") {
        document.getElementById("message").textContent = "Please fill all the fields!";
        return;
    }

    verificationCode = Math.floor(100000 + Math.random() * 900000).toString();
    console.log("Verification Code:", verificationCode);  // Simulating code sent




    document.getElementById("message").textContent = " ";
    document.getElementById("authForm").style.display = "block";
}

let authenticate = async () => {

    const popup = document.getElementById('authenticate-popup');
    const enteredCode = document.getElementById("code").value;
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;


    popup.classList.add('show');
    popup.querySelector("p").innerText = "Authenticating..."


    await setTimeout(() => {
        popup.classList.remove('show');

        if (enteredCode === verificationCode) {
            document.getElementById("message").textContent = "Login successful!";
            document.getElementById("message").style.color = "green";
            localStorage.setItem("username", username);
            localStorage.setItem("password", password);
            window.location.href = '/index.html';
        } else {
            document.getElementById("message").textContent = "Invalid code!";
        }
    }, 5000);
}

async function showConfirmation() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const popup = document.getElementById('authenticate-popup');

    console.log(popup.querySelector("p"))
    if (!isGmail()) {
        popup.classList.add('show');
        popup.querySelector("p").innerHTML = "invalid email id"
        await setTimeout(() => {
            popup.classList.remove('show');
        },5000);
    }

    else if (username === "" || password === "") {
        document.getElementById("message").textContent = "Please fill all fields!";
    }
    else{
    // Show the confirmation popup
    document.getElementById("confirmationPopup").style.display = "flex";
    document.getElementById("confirmationText").textContent =
        `The App Password you entered is correct?`;
    }
}

function proceed() {
    // Close the popup
    document.getElementById("confirmationPopup").style.display = "none";
    const username = document.getElementById("username").value;

    sendCode();
    authenticate_with_code(verificationCode);
    // Show authentication form
    document.getElementById("authForm").style.display = "block";
}

function closePopup() {
    document.getElementById("confirmationPopup").style.display = "none";
}

let removeWhiteSpace = () => {
    let password = document.querySelector("#password");

    let tmp = password.value.trim();
    password.value = ""
    for (let i = 0; i < tmp.length; i++)
        tmp[i] != ' ' ? password.value += tmp[i] : password.value;

}

function isGmail() {
    const gmailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    const email = document.getElementById("username");
    if (!gmailRegex.test(email.value)) {
        username.style = "border:2px solid red";
        return false;
    }
    else {
        username.style = "border:2px solid rgb(102, 102, 233);";
    }
    return true;
}

let toggle = () => {
    let passwordInput = document.getElementById("password");

    let toggleButton = document.getElementById("svg-image");

    if (passwordInput.type === "password") {
        passwordInput.type = "text";
        toggleButton.src = "./photos/eye-password-hide-svgrepo-com.svg"; // Change icon
    } else {
        passwordInput.type = "password";
        toggleButton.src = "./photos/eye-password-show-svgrepo-com.svg"; // Change back
    }
}


