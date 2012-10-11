/**
 * Experiments controller. To add/edit/delete experiments.
 */
Ext.define('eSalsa.controller.Experiments', {
    extend : 'Ext.app.Controller',
    views : [ 'experiments.List' ],
    stores : [ 'Experiments' ],
    init : function() {
        this.control({
            'experimentslist' : {
                edit : this.saveExperiment,
                deleteExperiment : this.deleteExperiment
            }
        });
    },
    /**
     * Save experiment to server.
     *
     * @param editor
     * @param e
     * @param e.record
     */
    saveExperiment : function(editor, e) {
        e.record.save({
            callback : function() {
                e.record.commit();
            }
        });
    },
    /**
     * Deletes experiment from server.
     * @param rec
     */
    deleteExperiment : function(rec) {
        var me = this;
        rec.destroy({
            callback : function() {
                me.getExperimentsStore().load();
            }
        });
    }
});