if (localStorage.getItem("username")) {
    document.querySelector("ul").innerHTML =
        `<li><a href="./index.html">Home</a></li>
                <li><a href="./email_send.html">Compose</a></li>
                <li><a href="./email_draft.html">Drafts</a></li>
                <li><a href="./email_sent_dashboard.html">Dashboard</a></li>
                <li><a href="#" class="logout">Logout</a></li>
                `;

}
else {
    window.location.href = "./email_login.html";
}


let selectedFiles = [];
let website_url = "https://emailtracker.up.railway.app";

let editor = document.querySelector(".ql-editor");
let params = new URLSearchParams(window.location.search);
let subject = document.querySelector("#subject")
let receiver = document.querySelector("#to");

let clean = () => {
    editor.placeholder = "Write your message here...";
    editor.innerHTML = "";

    username.innerHTML = localStorage.getItem("username");
}
clean();

(async () => {
    if (params.get("message-id")) {

        function base64ToFile(base64, fileName, mimeType) {
            // Decode Base64
            let byteCharacters = atob(base64);
            let byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            let byteArray = new Uint8Array(byteNumbers);

            // Create a Blob and File
            let fileBlob = new Blob([byteArray], { type: mimeType });
            return new File([fileBlob], fileName, { type: mimeType });
        }

        let message_id = params.get("message-id");
        const popup = document.getElementById('logout-popup');
    popup.querySelector("p").innerText="loading, please wait...";
    popup.classList.add('show');

        let mail_object = await fetch(website_url + `/get-single-mail?message-id=${message_id}`)
            .then(res => res.json());

        console.log(mail_object)

        editor.innerHTML = removeHiddenImages(mail_object.text);
        receiver.value = mail_object.receiverAddress;


        subject.value = mail_object.subject;
        let temp
        mail_object.attachedFiles.forEach(file => {
            let fileBytes = file.fileBytes;
            let fileName = file.fileName;
            let fileType = file.fileType;

            selectedFiles = [...selectedFiles, base64ToFile(fileBytes, fileName, fileType)];
            updateFileList();
        })
        popup.classList.remove('show');
    }
})();


function removeHiddenImages(htmlString) {
    // Match <img> tags where display="none" (single or double quotes)
    return htmlString.replace(/<img[^>]*>(?!.*<img)/, '');
}


// document.addEventListener("DOMContentLoaded", function () {
let fileInput = document.getElementById("fileInput");
let filePopup = document.getElementById("filePopup");
let fileList = document.getElementById("fileList");
let showFiles = document.getElementById("showFiles");
let closePopup = document.getElementById("closePopup");
let removeAll = document.getElementById("removeAll");


fileInput.addEventListener("change", function (event) {
    let files = Array.from(event.target.files);
    selectedFiles = [...selectedFiles, ...files];  // Append new files
    updateFileList();
});

function updateFileList() {
    fileList.innerHTML = "";

    if (selectedFiles.length > 0) {
        showFiles.innerText = "See Attached Files";
    } else {
        showFiles.innerText = "No files selected";
        filePopup.style.display = "none";
    }

    selectedFiles.forEach((file, index) => {
        let li = document.createElement("li");
        let fileURL = URL.createObjectURL(file);

        li.innerHTML = `<div>
        <a href="${fileURL}" download="${file.name}">
            ${index + 1}. ${file.name} (${(file.size / 1024).toFixed(2)} KB)
        </a>
        </div>
        <button class="remove-btn" data-index="${index}">Remove</button>
            `;
        fileList.appendChild(li);
    });

    document.querySelectorAll(".remove-btn").forEach(btn => {
        btn.addEventListener("click", function () {
            let index = parseInt(this.getAttribute("data-index"));
            removeFile(index);
        });
    });
}

function removeFile(index) {
    selectedFiles.splice(index, 1);
    updateFileList();
}

removeAll.addEventListener("click", function () {
    selectedFiles = [];
    updateFileList();
});

showFiles.addEventListener("click", function (event) {
    event.preventDefault();
    if (selectedFiles.length > 0) {
        filePopup.style.display = "flex";
    }
});

closePopup.addEventListener("click", function () {
    filePopup.style.display = "none";
});

// });



let send = async (to, subject, text) => {
    let formData = new FormData();

    // Append text fields
    formData.append("username", localStorage.getItem("username"));
    formData.append("password", localStorage.getItem("password"));
    formData.append("to", to);
    formData.append("subject", subject);
    formData.append("text", text);

    // Append files if any
    if (selectedFiles.length > 0) {
        for (let file of selectedFiles) {
            formData.append("files", file); // Backend should handle 'files' as an array
            // await sendFileToBackend(file); // If this is necessary
        }
    }

    // Send request with FormData
    return fetch(website_url + "/send", {
        method: 'POST',
        body: formData // No need to set `Content-Type`, browser sets it automatically
    })
        .then(response => response.json())
        .then(result => result);
};




let save_as_draft = (to, subject, text) => {

    let formData = new FormData();

    // Append text fields
    formData.append("username", localStorage.getItem("username"));
    formData.append("password", localStorage.getItem("password"));
    formData.append("to", to);
    formData.append("subject", subject);
    formData.append("text", text);

    // Append files if any
    if (selectedFiles.length > 0) {
        for (let file of selectedFiles) {
            formData.append("files", file); // Backend should handle 'files' as an array
            // await sendFileToBackend(file); // If this is necessary
        }
    }

    return fetch(website_url + "/save-as-draft", {
        method: 'POST',
        body: formData
    })
        .then(response => response.json())
        .then(result => result);

}



document.querySelector('.send-btn').addEventListener('click', async () => {
    const popup = document.getElementById('popup');
    popup.querySelector("p").innerHTML = "Sending..."
    let to = document.querySelector("#to").value.trim();
    let subject = document.querySelector("#subject").value.trim();
    let text = document.querySelector(".ql-editor").innerHTML;


    if (to == "") {
        alert("reciver address is missing!");
        return;
    }
    if (subject == "") {
        alert("subject is missing!");
        return;
    }

    console.log(to, subject, text);


    popup.classList.add('show');

    let id = -1;
    id = await send(to, subject, text);
    console.log(id)
    await setTimeout(() => {

        popup.classList.remove('show');

        if (id == 1) {
            window.location.href = "./email_sent_dashboard.html";
        }
        else if (id == 0) {
            popup.querySelector("p").innerHTML = "invalid email address."
        }
        else {
            alert("Some Error Occured! Check Your Internet Connection.\nPlease Try later !")
        }
    }, 1000);
    // Simulate sending process for 2 seconds




});




document.querySelector(".logout").addEventListener("click", () => {
    const popup = document.getElementById('logout-popup');
    popup.querySelector("p").innerText="logging you out...";

    popup.classList.add('show');
    setTimeout(() => {


        popup.classList.remove('show');

        window.location.href = "./logout.html"
    }, 5000);
})


document.querySelector('.draft').addEventListener('click', async () => {
    const popup = document.getElementById('popup');
    const loader = document.querySelector('.loader');
    popup.querySelector("p").innerHTML = "Saving as draft..."
    let to = document.querySelector("#to").value.trim();
    let subject = document.querySelector("#subject").value.trim();
    let text = document.querySelector(".ql-editor").innerHTML;


    if (to == "") {
        alert("reciver address is missing!");
        return;
    }
    if (subject == "") {
        alert("subject is missing!");
        return;
    }

    console.log(to, subject, text);


    popup.classList.add('show');

    let id = -1;
    id = await save_as_draft(to, subject, text);
    console.log(id)
    await setTimeout(() => {

        popup.classList.remove('show');

        if (id == 1) {
            window.location.href = "./email_draft.html";
        }
        else if (id == 0) {
            popup.querySelector("p").innerHTML = "invalid email address."
        }
        else {
            alert("Some Error Occured! Check Your Internet Connection.")
        }
    }, 2000);
    // Simulate sending process for 2 seconds




});




document.querySelector(".logout").addEventListener("click", () => {
    const popup = document.getElementById('logout-popup');


    popup.classList.add('show');
    setTimeout(() => {


        popup.classList.remove('show');

        window.location.href = "./logout.html"
    }, 5000);
})



