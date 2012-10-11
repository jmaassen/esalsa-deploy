/**
 * Tab to configure inputs.
 * Consist of a grid to select inputs and a form to edit an input.
 */
Ext.define('eSalsa.view.inputs.Tab', {
    extend : 'Ext.panel.Panel',
    alias : 'widget.inputstab',
    title : 'Inputs',
    layout : 'border',
    requires : [ 'eSalsa.view.inputs.List', 'eSalsa.view.inputs.Form',
            'Ext.layout.container.Border' ],
    items : [ {
        xtype : 'inputslist',
        region : 'west',
        header : false,
        collapsible : true,
        hideCollapseTool : true,
        split : true,
        width : 200
    }, {
        xtype : 'inputsform',
        region : 'center'
    } ]
});