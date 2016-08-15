package net.datamanager.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.ui.*;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import java.util.List;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    private final CustomerRepository repo;

    private final CustomerEditor editor;

    private final Grid customersGrid;

    private final TextField filter;

    private final Button addNewButton;

    private final Grid visualisationGridByAge;

    private final Grid visualisationGridBySex;

    private CustomerControl customerControl;

    @Autowired
    public VaadinUI(CustomerRepository repo, CustomerEditor editor) {
        this.repo = repo;
        this.editor = editor;
        this.customersGrid = new Grid();
        this.filter = new TextField();
        this.addNewButton = new Button("New customer", FontAwesome.PLUS);
        this.visualisationGridByAge = new Grid("Graph by age group");
        this.visualisationGridBySex = new Grid("Graph by sex");

        // Let's make life easier to the QAs
        addNewButton.setId("Add new customer");
        customersGrid.setId("Customers list grid");
        visualisationGridBySex.setId("Grid by sex");
        visualisationGridByAge.setId("Grid by age");
    }

    @Override
    protected void init(VaadinRequest request) {
        // build layout
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addStyleName("outlined");
        horizontalLayout.setSizeFull();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);

        generateCustomerControlFrame(horizontalLayout);
        generateVisualizationByAgeFrame(horizontalLayout);
        generateVisualizationBySexFrame(horizontalLayout);

        setContent(horizontalLayout);
    }

    private void generateCustomerControlFrame(HorizontalLayout horizontalLayout) {
        customerControl = getCustomerControl();
        horizontalLayout.addComponent(customerControl);
        horizontalLayout.setExpandRatio(customerControl, 1);
        customerControl.setWidth(100, Unit.PERCENTAGE);
        customerControl.setHeight(100, Unit.PERCENTAGE);
    }

    private void generateVisualizationByAgeFrame(HorizontalLayout horizontalLayout) {
        horizontalLayout.addComponent(visualisationGridByAge);
        horizontalLayout.setExpandRatio(visualisationGridByAge, 1);
        visualisationGridByAge.setWidth(100, Unit.PERCENTAGE);
        visualisationGridByAge.setHeight(100, Unit.PERCENTAGE);
        visualisationGridByAge.setColumns("Age Group", "Count");
    }

    private void generateVisualizationBySexFrame(HorizontalLayout horizontalLayout) {
        horizontalLayout.addComponent(visualisationGridBySex);
        horizontalLayout.setExpandRatio(visualisationGridBySex, 1);
        visualisationGridBySex.setWidth(100, Unit.PERCENTAGE);
        visualisationGridBySex.setHeight(100, Unit.PERCENTAGE);
        visualisationGridBySex.setColumns("Sex", "Count");
    }

    private void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            customersGrid.setContainerDataSource(
                    new BeanItemContainer<>(Customer.class, repo.findAll()));
        }
        else {
            customersGrid.setContainerDataSource(new BeanItemContainer<>(Customer.class,
                    repo.findByLastNameStartsWithIgnoreCase(text)));
        }
    }

//    private void countCustomersBySex() {
//        visualisationGridBySex.setContainerDataSource(
//                    new BeanItemContainer(List.class, repo.countBySex()));
//    }

    private CustomerControl getCustomerControl() {
        if (customerControl == null ) {
            customerControl = new CustomerControl();
            customerControl.setWidthUndefined();
        }
        return customerControl;
    }

    private class CustomerControl extends CustomComponent {

        CustomerControl() {
            Panel panel = new Panel("Customers");
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setMargin(true);
            panel.setContent(verticalLayout);

            verticalLayout.addComponent(filter);
            verticalLayout.addComponent(customersGrid);
            verticalLayout.addComponent(addNewButton);

            customersGrid.setHeight(90, Unit.PERCENTAGE);
            verticalLayout.setExpandRatio(customersGrid, 1);
            verticalLayout.setExpandRatio(addNewButton, 0.1f);
            verticalLayout.setSizeFull();
            verticalLayout.setWidthUndefined();
            panel.setSizeFull();
            panel.setHeight(100, Unit.PERCENTAGE);

            customersGrid.setColumns("id", "firstName", "lastName", "sex", "age");
            filter.setInputPrompt("Filter by last name");

            // Replace listing with filtered content when user changes filter
            filter.addTextChangeListener(e -> listCustomers(e.getText()));

            // Connect selected Customer to editor or hide if none is selected
            customersGrid.addSelectionListener(e -> {
                if (e.getSelected().isEmpty()) {
                    editor.setVisible(false);
                }
                else {
                    editor.editCustomer((Customer) customersGrid.getSelectedRow());
                }
            });

            // Instantiate and edit new Customer the new button is clicked
            addNewButton.addClickListener(e -> editor.editCustomer(new Customer("", "",
                    null, null)));

            // Listen changes made by the editor, refresh data from backend
            editor.setChangeHandler(() -> {
                editor.setVisible(false);
                listCustomers(filter.getValue());
            });

            // Initialize listing
            listCustomers(null);

            setCompositionRoot(panel);
        }
    }

}