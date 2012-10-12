/**
 * Model of a worker.
 * Uses '/resource/workers' as base url for rest calls to get list and save changes.
 */
Ext.define('eSalsa.model.Worker', {
    extend : 'Ext.data.Model',
    fields : [ 'id', 'comment', 'uri', 'template_dir', 'input_dir',
            'output_dir', {name:'add_props', defaultValue:{}} ],
    proxy : {
        type : 'rest',
        url : "/resource/workers",
        reader : {
            type : 'json',
            root : 'rows',
            totalProperty : 'total'
        }
    }
});