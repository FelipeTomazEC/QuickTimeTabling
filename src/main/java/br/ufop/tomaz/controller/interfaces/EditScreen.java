package br.ufop.tomaz.controller.interfaces;

import br.ufop.tomaz.model.Editable;

public interface EditScreen {
    void loadEditable(Editable editable);

    void configureConfirmEditionButton();
}
