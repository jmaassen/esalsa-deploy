/**
 * Tabs for experiments and the parts of an experiment.
 */
Ext.define('eSalsa.view.Viewport', {
    extend : 'Ext.container.Viewport',
    layout : 'fit',
    requires : [ 'Ext.tab.Panel' ],
    items : [ {
        xtype : 'tabpanel',
        activeTab : 0,
        items : [ {
            xtype : 'experimentslist'
        }, {
            xtype : 'workerstab'
        }, {
            xtype : 'inputstab'
        }, {
            xtype : 'configurationstab'
        } ]
    } ]
});