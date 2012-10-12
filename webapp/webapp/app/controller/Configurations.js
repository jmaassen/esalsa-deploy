/**
 * Configurations controller. To add/edit/delete configurations.
 */
Ext.define('eSalsa.controller.Configurations', {
    extend: 'Ext.app.Controller',
    views: ['configurations.Tab'],
    refs: [{
        ref: 'list', selector: 'configurationslist'
    }, {
        ref: 'form', selector: 'configurationsform'
    }],
    stores: ['Configurations'],
    init: function() {
        this.control({
            'configurationslist': {
                select: this.onSelect,
                deselect: this.onDeselect,
                beforedeselect: this.onBeforeDeselect
            },
            'configurationslist component[action=add-conf]' : {
                click : this.addConf
            },
            'configurationsform component[action=save-conf]' : {
                click : this.saveConf
            },
            'configurationsform component[action=del-conf]' : {
                click : this.deleteConf
            }
        });
    },
    /**
     * On selection of a configuration in the list, loads configuration into enabled form.
     * @param rm
     * @param record
     */
    onSelect: function(rm, record) {
        this.getForm().loadRecord(record);
        this.getForm().enable();
    },
    /**
     * On deselect of configuration in the list, disables and clears form.
     * @param rm
     * @param record
     */
    onDeselect: function(rm, record) {
        this.getForm().disable();
        this.getForm().reset();
    },
    /**
     * Just before configuration is deselected, checks if form has been changed.
     * @param rm
     * @param record
     */
    onBeforeDeselect: function(rm, record) {
        // TODO if unsaved changes then give prompt
    },
    /**
     * Saves configuration to server.
     */
    saveConf : function() {
        var conf = Ext.create('eSalsa.model.Configuration', this.getForm()
                .getValues(false, false, false, true));
        conf.save();
    },
    /**
     * Deletes selected configuration in form on server.
     */
    deleteConf : function() {
        var me = this;
        var conf = Ext.create('eSalsa.model.Configuration', this.getForm()
                .getValues(false, false, false, true));
        conf.destroy({callback: function(conf) {
            me.getConfigurationsStore().load();
        }});
    },
    /**
     * Gives prompt to a configuration by first entering it's ID and then selecting it to show the configuration in the form.
     */
    addConf : function() {
        var me = this;
        Ext.Msg.prompt('Add conf', 'Please enter conf ID:', function(btn,
                id) {
            if (btn == 'ok') {
                var conf = Ext.create('eSalsa.model.Configuration', {
                    id : id
                });
                me.getConfigurationsStore().add(conf);
                me.getList().getSelectionModel().select(conf);
            }
        });
    }
});