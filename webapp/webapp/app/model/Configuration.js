/**
 * Model of a configuration.
 * Uses '/resource/configurations' as base url for rest calls to get list and save changes.
 */
Ext.define('eSalsa.model.Configuration', {
    extend : 'Ext.data.Model',
    fields : [ 'id', 'comment', 'configuration' ],
    proxy : {
        type : 'rest',
        url : '/resource/configurations',
        reader : {
            type : 'json',
            root : 'rows',
            totalProperty : 'total'
        }
    }
});