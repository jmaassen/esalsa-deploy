/**
 * Model of a experiment.
 * Uses '/resource/experiments' as base url for rest calls to get list and save changes.
 */
Ext.define('eSalsa.model.Experiment', {
    extend : 'Ext.data.Model',
    fields : [ 'id', 'comment', 'worker', 'configuration', 'input' ],
    proxy : {
        type : 'rest',
        url : '/resource/experiments',
        reader : {
            type : 'json',
            root : 'rows',
            totalProperty : 'total'
        }
    }
});