/**
 * List of configurations
 */
Ext.define('eSalsa.view.configurations.List', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.configurationslist',
    store : 'Configurations',
    allowDeselect : true,
    initComponent : function() {

        this.columns = [ {
            text : 'ID',
            dataIndex : 'id',
            flex : 1
        } ];

        this.tbar = [ {
            text : 'Add Configuration',
            iconCls : 'icon-add',
            action : 'add-conf'
        } ];

        this.callParent(arguments);
    }
});