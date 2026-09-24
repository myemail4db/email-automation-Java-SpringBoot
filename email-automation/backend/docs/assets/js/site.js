
document.addEventListener("DOMContentLoaded",()=>{
  const current=location.pathname.split("/").pop()||"index.html";
  document.querySelectorAll(".sidebar a").forEach(a=>{
    const href=a.getAttribute("href")||"";
    if(href.split("/").pop()===current)a.classList.add("active");
  });
});
