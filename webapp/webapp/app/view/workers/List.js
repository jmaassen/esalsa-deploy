/**
 * List of workers.
 */
Ext.define('eSalsa.view.workers.List', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.workerslist',
    store : 'Workers',
    allowDeselect : true,
    initComponent : function() {

        this.columns = [ {
            text : 'ID',
            dataIndex : 'id',
            flex : 1
        } ];

        this.tbar = [ {
            text : 'Add Worker',
            iconCls : 'icon-add',
            action : 'add-worker'
        } ];

        this.callParent(arguments);
    }
});