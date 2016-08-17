package net.datamanager.application;


import com.vaadin.data.Validator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.*;
import io.codearte.jfairy.producer.person.Person;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.themes.ValoTheme;

import static io.codearte.jfairy.producer.person.Person.Sex.FEMALE;
import static io.codearte.jfairy.producer.person.Person.Sex.MALE;

@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout {

    private final CustomerRepository repository;

    /**
     * The currently edited customer
     */
    private Customer customer;

    /* Fields to edit properties in Customer entity */
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private NativeSelect sex = new NativeSelect("Sex");
    private TextField age = new TextField("Age");

    /* Action buttons */
    private Button save = new Button("Save", FontAwesome.SAVE);
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", FontAwesome.TRASH_O);
    private CssLayout actions = new CssLayout(save, cancel, delete);

    @Autowired
    public CustomerEditor(CustomerRepository repository) {
        this.repository = repository;

        addComponents(firstName, lastName, sex, age, actions);

        // Configure and style components
        setSpacing(true);
        actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        markFieldsRequired();

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> handleSaveCustomer(customer));
        delete.addClickListener(e -> handleDeleteCustomer(customer));
        cancel.addClickListener(e -> handleCloseEditDialog());
    }

    private void markFieldsRequired() {
        firstName.setRequired(true);
        lastName.setRequired(true);
        sex.setRequired(true);
        sex.addItems(Person.Sex.values());
        age.setRequired(true);
    }

    private void handleSaveCustomer(Customer customer) {
        if (areFieldsValid()) {
            repository.save(customer);
            setVisible(false);
        }
    }

    private boolean areFieldsValid() {
        try {
            firstName.validate();
            lastName.validate();
            sex.validate();
            age.validate();
        }
        catch (Validator.EmptyValueException e) {
            Notification.show("Unable to save a customer: please fill all the required fields.");
            return false;
        }
        return true;
    }

    private void handleDeleteCustomer(Customer customer) {
        repository.delete(customer);
        setVisible(false);
    }

    private void handleCloseEditDialog() {
        setVisible(false);
    }

    public interface ChangeHandler {

        void onChange();
    }

    public final void editCustomer(Customer c) {
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            customer = repository.findOne(c.getId());
        } else {
            customer = c;
        }
        cancel.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        BeanFieldGroup.bindFieldsUnbuffered(customer, this);

        setVisible(true);

        // A hack to ensure the whole form is visible
        save.focus();
        // Select all text in firstName field automatically
        firstName.selectAll();
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete is clicked
        save.addClickListener(e -> h.onChange());
        delete.addClickListener(e -> h.onChange());
    }

}