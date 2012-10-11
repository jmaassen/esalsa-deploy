/**
 * Model of an input.
 * Uses '/resource/inputs' as base url for rest calls to get list and save changes.
 */
Ext.define('eSalsa.model.Input', {
  extend : 'Ext.data.Model',
  fields : [ 'id', 'comment', { name:'files', defaultValue:[]} ],
  proxy : {
        type : 'rest',
        url : "/resource/inputs",
        reader : {
            type : 'json',
            root : 'rows',
            totalProperty : 'total'
        }
    }
});