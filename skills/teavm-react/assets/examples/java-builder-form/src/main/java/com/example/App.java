package com.example;

import ca.weblite.teavmreact.core.React;
import ca.weblite.teavmreact.core.ReactDOM;
import ca.weblite.teavmreact.core.ReactElement;
import ca.weblite.teavmreact.hooks.Hooks;
import ca.weblite.teavmreact.hooks.StateHandle;
import ca.weblite.teavmreact.html.DomBuilder.*;
import org.teavm.jso.JSObject;
import org.teavm.jso.dom.html.HTMLDocument;

import static ca.weblite.teavmreact.html.Html.*;

// Contact form demonstrating DomBuilder DSL with controlled inputs.
public class App {

    private static ReactElement contactForm(JSObject props) {
        StateHandle<String> name = Hooks.useState("");
        StateHandle<String> email = Hooks.useState("");
        StateHandle<String> message = Hooks.useState("");
        StateHandle<Boolean> submitted = Hooks.useState(false);

        // Show confirmation after submit
        if (submitted.getBool()) {
            return Div.create()
                .className("form-success")
                .child(H2.create().text("Thank you, " + name.getString() + "!").build())
                .child(P.create().text("We received your message and will reply to " + email.getString() + ".").build())
                .child(Button.create()
                    .text("Send another")
                    .onClick(e -> submitted.setBool(false))
                    .build())
                .build();
        }

        // Build the form using DomBuilder DSL
        return Div.create()
            .className("contact-form")
            .child(H1.create().text("Contact Us").build())

            // Name field
            .child(Div.create().className("field")
                .child(Label.create().text("Name").build())
                .child(Input.text()
                    .value(name.getString())
                    .placeholder("Your name")
                    .onChange(e -> name.setString(e.getTarget().getValue()))
                    .build())
                .build())

            // Email field
            .child(Div.create().className("field")
                .child(Label.create().text("Email").build())
                .child(Input.text()
                    .value(email.getString())
                    .placeholder("you@example.com")
                    .onChange(e -> email.setString(e.getTarget().getValue()))
                    .build())
                .build())

            // Message field
            .child(Div.create().className("field")
                .child(Label.create().text("Message").build())
                .child(Textarea.create()
                    .value(message.getString())
                    .placeholder("Your message...")
                    .onChange(e -> message.setString(e.getTarget().getValue()))
                    .build())
                .build())

            // Submit button — disabled when required fields are empty
            .child(Button.create()
                .text("Submit")
                .disabled(name.getString().isEmpty() || email.getString().isEmpty())
                .onClick(e -> submitted.setBool(true))
                .build())
            .build();
    }

    public static void main(String[] args) {
        var form = React.wrapComponent(App::contactForm, "ContactForm");
        var root = ReactDOM.createRoot(
            HTMLDocument.current().getElementById("root")
        );
        root.render(component(form));
    }
}
