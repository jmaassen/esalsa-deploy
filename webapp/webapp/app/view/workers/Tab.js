/**
 * Tabs to configure workers.
 * Consists of a grid to select a worker and a form to edit/add a worker.
 */
Ext.define('eSalsa.view.workers.Tab', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.workerstab',
    title : 'Workers',
    layout : 'border',
    requires : [ 'eSalsa.view.workers.List', 'eSalsa.view.workers.Form',
            'Ext.layout.container.Border' ],
    items : [ {
        xtype : 'workerslist',
        region : 'west',
        header : false,
        collapsible : true,
        hideCollapseTool : true,
        split : true,
        width : 200
    }, {
        xtype : 'workersform',
        region : 'center'
    } ]
});