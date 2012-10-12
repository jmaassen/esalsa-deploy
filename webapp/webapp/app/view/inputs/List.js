/**
 * List of inputs.
 */
Ext.define('eSalsa.view.inputs.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.inputslist',
    store: 'Inputs',
    allowDeselect: true,
    initComponent: function() {

        this.columns = [{
          text: 'ID', dataIndex: 'id', flex: 1
        }];

        this.tbar = [ {
            text : 'Add Input',
            iconCls : 'icon-add',
            action : 'add-input'
          } ];

        this.callParent(arguments);
    }
});