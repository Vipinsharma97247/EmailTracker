if (localStorage.getItem("username")) {
    document.querySelector("ul").innerHTML =
        `<li><a href="./index.html">Home</a></li>
                <li><a href="./email_send.html">Compose</a></li>
                <li><a href="./email_draft.html">Drafts</a></li>
                <li><a href="./email_sent_dashboard.html">Dashboard</a></li>
                <li class="user-menu">
                    <a href="#">User</a>
                    <ul class="dropdown">
                        <li><a href="email_user_dashboard.html">User Dashboard</a></li>
                        <hr>
                        <li><a href="#" class="logout" style="color:rgb(253, 97, 97)">Logout</a></li>
                    </ul>
                </li>
                `;

}
else{
    window.location.href="./email_login.html";
}

let minValue = 0;  // Change this as needed
let maxValue = 10; // Change this as needed

function updateGauge(value) {
    // let slider = document.getElementById("slider");
    let needle = document.getElementById("needle");
    let progressArc = document.getElementById("progressArc");
    // let counter = document.getElementById("counter");
    let valueDisplay = document.getElementById("valueDisplay");

    
    // Normalize value within min-max range
    let percentage = (value - minValue) / (maxValue - minValue);

    // Needle rotates from -90° to 90°
    let angle = -90 + (percentage * 180);

    // Adjust stroke offset to fill the arc correctly
    let maxDashOffset = 283;
    let dashOffset = maxDashOffset - (percentage * maxDashOffset);

    // Apply transformations
    needle.style.transform = `translateX(-50%) rotate(${angle}deg)`;
    progressArc.style.strokeDashoffset = dashOffset;

    // Update displayed value
    // counter.textContent = value;
    valueDisplay.textContent = value;

    // Position value text dynamically at a fixed distance from needle tip
    let radians = angle * (Math.PI / 180);
    let radius = 65;
    let x = 100 + radius * Math.sin(radians);
    let y = 85 - radius * Math.cos(radians);

    valueDisplay.style.left = `${x}px`;
    valueDisplay.style.top = `${y}px`;
}

// Set slider range dynamically
// document.getElementById("slider").min = minValue;
// document.getElementById("slider").max = maxValue;
// document.getElementById("slider").value = minValue;
document.getElementById("min-value").innerText = minValue;
document.getElementById("max-value").innerText = maxValue;
updateGauge(5);
