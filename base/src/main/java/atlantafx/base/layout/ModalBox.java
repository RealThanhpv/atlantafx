/* SPDX-License-Identifier: MIT */

package atlantafx.base.layout;

import atlantafx.base.controls.ModalPane;
import java.util.Objects;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.Nullable;

/**
 * The ModalBox is a specialized control or layout designed to hold the
 * {@link ModalPane} dialog content. It includes the close button out-of-the-box
 * and allows for the addition of arbitrary children. The ModalBox is derived
 * from the {@link AnchorPane}, so it inherits the same API. Just be sure that
 * you haven't removed the close button while using it.
 */
public class ModalBox extends AnchorPane {

    protected final StackPane closeButton = new StackPane();
    protected final StackPane closeButtonIcon = new StackPane();
    protected final @Nullable String selector;
    protected @Nullable ModalPane modalPane;

    /**
     * Creates a ModalBox layout.
     */
    public ModalBox() {
        this((String) null);
    }

    /**
     * Creates a ModalBox layout with the given children.
     *
     * @param children the initial set of children for this pane
     */
    public ModalBox(Node... children) {
        this((String) null, children);
    }

    /**
     * Creates a ModalBox layout with the given children and binds
     * the close handler to a ModalPane via CSS selector. When user clicks
     * on the close button, it performs a ModalPane lookup via the specified
     * selector and calls the {@link ModalPane#hide()} method automatically.
     *
     * @param selector the ModalPane pane CSS selector
     * @param children the initial set of children for this pane
     */
    public ModalBox(@Nullable @NamedArg("selector") String selector, Node... children) {
        super(children);

        this.selector = selector;
        this.modalPane = null;

        createLayout();
    }

    /**
     * Creates a ModalBox layout with the given children and binds
     * the close handler to a ModalPane. When user clicks on the close button,
     * it calls the {@link ModalPane#hide()} method automatically.
     *
     * @param modalPane the ModalPane pane CSS selector
     * @param children  the initial set of children for this pane
     */
    public ModalBox(@Nullable ModalPane modalPane, Node... children) {
        super(children);

        this.selector = null;
        this.modalPane = modalPane;

        createLayout();
    }

    /**
     * Adds (prepends) specified node to the ModalBox before the close button.
     *
     * <p>Otherwise, if the added node takes up the full size of the ModalBox
     * and {@link Node#isMouseTransparent()} is false, then the close button
     * will not receive mouse events and therefore will not be clickable.
     *
     * @param node the node to be added
     */
    public void addContent(Node node) {
        Objects.requireNonNull(node, "Node cannot be null.");
        getChildren().add(getChildren().indexOf(closeButton), node);
    }

    /**
     * Manually closes the ModalBox in case one needs to use their
     * own close button.
     */
    public void close() {
        handleClose();
    }

    protected void createLayout() {
        closeButton.getStyleClass().add("close-button");
        closeButton.getChildren().setAll(closeButtonIcon);
        closeButton.setOnMouseClicked(e -> handleClose());

        closeButtonIcon.getStyleClass().add("icon");

        getStyleClass().add("modal-box");
        getChildren().add(closeButton);
        setCloseButtonPosition();
    }

    protected void setCloseButtonPosition() {
        setTopAnchor(closeButton, 10d);
        setRightAnchor(closeButton, 10d);
    }

    protected void handleClose() {
        if (modalPane != null) {
            modalPane.hide(clearOnClose.get());
        } else if (selector != null && getScene() != null) {
            if (getScene().lookup(selector) instanceof ModalPane mp) {
                // cache modal pane so that the lookup is executed only once
                modalPane = mp;
                modalPane.hide(clearOnClose.get());
            }
        }

        // call user specified close handler
        if (onClose.get() != null) {
            onClose.get().handle(new Event(Event.ANY));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The property representing the user specified close handler. Note that
     * if you have also specified the ModalPane instance or CSS selector, this
     * handler will be executed after the default close handler. Therefore, you
     * can use it to perform arbitrary actions on dialog close.
     */
    protected final ObjectProperty<EventHandler<? super Event>> onClose =
        new SimpleObjectProperty<>(this, "onClose");

    public EventHandler<? super Event> getOnClose() {
        return onClose.get();
    }

    public ObjectProperty<EventHandler<? super Event>> onCloseProperty() {
        return onClose;
    }

    public void setOnClose(EventHandler<? super Event> onClose) {
        this.onClose.set(onClose);
    }

    /**
     * See {@link ModalPane#hide(boolean)}.
     */
    protected final BooleanProperty clearOnClose = new SimpleBooleanProperty(this, "clearOnClose");

    public boolean isClearOnClose() {
        return clearOnClose.get();
    }

    public BooleanProperty clearOnCloseProperty() {
        return clearOnClose;
    }

    public void setClearOnClose(boolean clearOnClose) {
        this.clearOnClose.set(clearOnClose);
    }
}
