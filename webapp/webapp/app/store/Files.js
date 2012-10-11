/**
 * Store for files.
 */
Ext.define('eSalsa.store.Files', {
  extend : 'Ext.data.ArrayStore',
  model : 'eSalsa.model.File',
  proxy: {
      type: 'memory'
  }
});