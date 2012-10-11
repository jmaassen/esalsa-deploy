/**
 * Worker controller. To add/edit/delete workers.
 */
Ext.define('eSalsa.controller.Workers', {
    extend : 'Ext.app.Controller',
    views : [ 'workers.Tab' ],
    refs : [ {
        ref : 'list',
        selector : 'workerslist'
    }, {
        ref : 'form',
        selector : 'workersform'
    } ],
    stores : [ 'Workers' ],
    init : function() {
        this.control({
            'workerslist' : {
                select : this.onSelect,
                deselect : this.onDeselect,
                beforedeselect : this.onBeforeDeselect
            },
            'workerslist component[action=add-worker]' : {
                click : this.addWorker
            },
            'workersform component[action=save-worker]' : {
                click : this.saveWorker
            },
            'workersform component[action=del-worker]' : {
                click : this.deleteWorker
            }
        });
    },
    /**
     * On selection of a worker in the list, loads worker into enabled form.
     * @param rm
     * @param record
     */
    onSelect : function(rm, record) {
        this.getForm().loadRecord(record);
        this.getForm().enable();
    },
    /**
     * On deselect of worker in the list, disables and clears form.
     * @param rm
     * @param record
     */
    onDeselect : function(rm, record) {
        this.getForm().disable();
        this.getForm().reset();
    },
    /**
     * Just before worker is deselected, checks if form has been changed.
     * @param rm
     * @param record
     */
    onBeforeDeselect : function(rm, record) {
        // TODO if unsaved changes then give prompt
    },
    /**
     * Saves worker to server.
     */
    saveWorker : function() {
        var worker = Ext.create('eSalsa.model.Worker', this.getForm()
                .getValues(false, false, false, true));
        worker.save();
    },
    /**
     * Deletes selected worker in form on server.
     */
    deleteWorker : function() {
        var me = this;
        var worker = Ext.create('eSalsa.model.Worker', this.getForm()
                .getValues(false, false, false, true));
        worker.destroy({callback: function(worker) {
            me.getWorkersStore().load();
        }});
    },
    /**
     * Gives prompt to a worker by first entering it's ID and then selecting it to show the worker in the form.
     */
    addWorker : function() {
        var me = this;
        Ext.Msg.prompt('Add worker', 'Please enter worker ID:', function(btn,
                id) {
            if (btn == 'ok') {
                var worker = Ext.create('eSalsa.model.Worker', {
                    id : id
                });
                me.getWorkersStore().add(worker);
                me.getList().getSelectionModel().select(worker);
            }
        });
    }
});