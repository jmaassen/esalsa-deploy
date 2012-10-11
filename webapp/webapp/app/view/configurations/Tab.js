/**
 * Tab to configure configurations.
 * Consists of list to select configuration and form to edit configuration.
 */
Ext.define('eSalsa.view.configurations.Tab', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.configurationstab',
    title : 'Configurations',
    layout : 'border',
    requires : [ 'eSalsa.view.configurations.List',
            'eSalsa.view.configurations.Form', 'Ext.layout.container.Border' ],
    items : [ {
        xtype : 'configurationslist',
        region : 'west',
        header : false,
        collapsible : true,
        hideCollapseTool : true,
        split : true,
        width : 200
    }, {
        xtype : 'configurationsform',
        region : 'center'
    } ]
});