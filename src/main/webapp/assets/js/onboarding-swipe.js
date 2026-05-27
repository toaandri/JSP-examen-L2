(() => {
  const card = document.getElementById("interestSwipeCard") || document.getElementById("questionSwipeCard");
  if (!card) return;

  let startX = 0;
  let dragging = false;
  const nopeBtn = document.querySelector(".js-swipe-nope");
  const likeBtn = document.querySelector(".js-swipe-like");

  const onDown = (x) => {
    dragging = true;
    startX = x;
    card.style.transition = "none";
  };

  const onMove = (x) => {
    if (!dragging) return;
    const dx = x - startX;
    card.style.transform = `translateX(${dx}px) rotate(${dx / 18}deg)`;
    card.style.opacity = String(Math.max(0.75, 1 - Math.abs(dx) / 420));
  };

  const onUp = (x) => {
    if (!dragging) return;
    dragging = false;
    const dx = x - startX;
    card.style.transition = "transform .18s ease, opacity .18s ease";
    card.style.transform = "";
    card.style.opacity = "";
    if (dx > 120 && likeBtn) likeBtn.click();
    if (dx < -120 && nopeBtn) nopeBtn.click();
  };

  card.addEventListener("mousedown", (e) => onDown(e.clientX));
  window.addEventListener("mousemove", (e) => onMove(e.clientX));
  window.addEventListener("mouseup", (e) => onUp(e.clientX));

  card.addEventListener("touchstart", (e) => onDown(e.touches[0].clientX), {passive: true});
  card.addEventListener("touchmove", (e) => onMove(e.touches[0].clientX), {passive: true});
  card.addEventListener("touchend", (e) => onUp((e.changedTouches && e.changedTouches[0]) ? e.changedTouches[0].clientX : startX));
})();

