function debounce(func, wait, immediate) {
    let timeout;
    return function() {
        let context = this, args = arguments;
        let later = function() {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        const callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
    };
};

$(document).ready(function () {

    $.ajaxSetup({
        beforeSend: xhr => xhr.setRequestHeader('Csrf-Token', $('input[name="csrfToken"]').attr('value'))
    })

    $("section").each((_, el) => {

        const codemirror = CodeMirror.fromTextArea($(el).find("textarea")[0], {
            mode: "text/x-sql",
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: true,
            extraKeys: {"Ctrl-Space": "autocomplete"},
            hintOptions: {
                tables: JSON.parse($("#schema").html())
            }
        })

        codemirror.setValue(localStorage.getItem(location.pathname + $(el).attr("id")) || "\n\n\n\n")

        $(el).find("textarea").on("keydown",
            debounce(() => localStorage.setItem(location.pathname + $(el).attr("id"), codemirror.getValue()), 1000)
        )

        const [button, spinner] = $(el).find("[data-submit] button").toArray()
        const result = $(el).find("[data-result]")
        const close = $(el).find("[data-close]")

        if(localStorage.getItem(location.pathname + $(el).attr("id") + "result")) {
            markSucess()
        }

        function toggle() {
            button.classList.toggle("d-none")
            spinner.classList.toggle("d-none")
        }

        function markSucess() {
            button.classList.remove("btn-primary")
            button.classList.add("btn-success")
            spinner.classList.remove("btn-primary")
            spinner.classList.add("btn-success")
        }

        close.on("click", () => {
            result.html("\n\n\n\n")
            close.addClass("d-none")
        });


        $(button).on("click", () => {
            toggle()
            close.addClass("d-none")
            result.html("\n\n\n\n")

            $.post("sql", {
                sql: codemirror.getValue(),
                verifingQuery: $(el).data("query")
            })
            .done(response => {
                result.html(response)
                close.removeClass("d-none")
                localStorage.setItem(location.pathname + $(el).attr("id") + "result", "ok")
                markSucess()
            })
            .fail(error => {
                close.removeClass("d-none")
                if(error.status === 406) {
                    result.html(error.responseText)
                }
            })
            .always(() => toggle())

        });

    })


})
