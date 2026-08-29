document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".toggle-form input[type='checkbox']").forEach(function (checkbox) {
        checkbox.addEventListener("change", function () {
            checkbox.closest("form").submit();
        });
    });

    document.querySelectorAll(".delete-form").forEach(function (form) {
        form.addEventListener("submit", function (e) {
            if (!confirm("이 할 일을 삭제하시겠습니까?")) {
                e.preventDefault();
            }
        });
    });

    document.querySelectorAll("[data-edit-trigger]").forEach(function (button) {
        button.addEventListener("click", function () {
            var item = button.closest(".todo-item");
            item.classList.add("editing");
            var editForm = item.querySelector(".edit-form");
            editForm.classList.add("editing");
            var input = editForm.querySelector("input[type='text']");
            input.focus();
            input.select();
        });
    });

    document.querySelectorAll("[data-edit-cancel]").forEach(function (button) {
        button.addEventListener("click", function () {
            var item = button.closest(".todo-item");
            item.classList.remove("editing");
            item.querySelector(".edit-form").classList.remove("editing");
        });
    });
});
