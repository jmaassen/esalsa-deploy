/**
 * Controller for inputs.
 */
Ext.define('eSalsa.controller.Inputs', {
    extend : 'Ext.app.Controller',
    views : [ 'inputs.Tab' ],
    refs : [ {
        ref : 'list',
        selector : 'inputslist'
    }, {
        ref : 'form',
        selector : 'inputsform'
    } ],
    stores : [ 'Inputs', 'Files' ],
    init : function() {
        this.control({
            'inputslist' : {
                select : this.onSelect,
                deselect : this.onDeselect,
                beforedeselect : this.onBeforeDeselect
            },
            'inputslist component[action=add-input]' : {
                click : this.addInput
            },
            'inputsform component[action=save-input]' : {
                click : this.saveInput
            },
            'inputsform component[action=del-input]' : {
                click : this.deleteInput
            }
        });
    },
    /**
     * On selection of a input in the list, loads input into enabled form.
     * @param rm
     * @param record
     */
    onSelect : function(rm, record) {
        this.getForm().loadRecord(record);
        this.getForm().enable();
    },
    /**
     * On deselect of input in the list, disables and clears form.
     * @param rm
     * @param record
     */
    onDeselect : function(rm, record) {
        this.getForm().disable();
        this.getForm().reset();
    },
    /**
     * Just before input is deselected, checks if form has been changed.
     * @param rm
     * @param record
     */
    onBeforeDeselect : function(rm, record) {
        // TODO if unsaved changes then give prompt
    },
    /**
     * Saves input to server.
     */
    saveInput : function() {
        var input = Ext.create('eSalsa.model.Input', this.getForm()
                .getValues(false, false, false, true));
        input.save();
    },
    /**
     * Deletes selected input on server.
     */
    deleteInput : function() {
        var me = this;
        var input = Ext.create('eSalsa.model.Input', this.getForm()
                .getValues(false, false, false, true));
        input.destroy({callback: function(input) {
            me.getInputsStore().load();
        }});
    },
    /**
     * Gives prompt to a input by first entering it's ID and then selecting it to show the input in the form.
     */
    addInput : function() {
        var me = this;
        Ext.Msg.prompt('Add input', 'Please enter input ID:', function(btn,
                id) {
            if (btn == 'ok') {
                var input = Ext.create('eSalsa.model.Input', {
                    id : id
                });
                me.getInputsStore().add(input);
                me.getList().getSelectionModel().select(input);
            }
        });
    }
});