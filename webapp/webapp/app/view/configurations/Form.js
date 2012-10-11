/**
 * Form to edit a configuration
 */
Ext.define('eSalsa.view.configurations.Form', {
    extend : 'Ext.form.Panel',
    alias : 'widget.configurationsform',
    defaultType : 'textfield',
    bodyPadding : 5,
    layout : 'anchor',
    disabled : true,
    defaults : {
        anchor : '100%',
        labelAlign : 'top'
    },
    items : [ {
        fieldLabel : 'ID',
        name : 'id',
        allowBlank : false
    }, {
        fieldLabel : 'Comment',
        name : 'comment',
        emptyText : 'Enter comment',
        xtype : 'textarea'
    }, {
        fieldLabel : 'Configuration',
        xtype : 'textarea', // TODO find editor which highlights code see
                            // http://stackoverflow.com/questions/1619167/textarea-that-can-do-syntax-highlighting-on-the-fly
        name : 'configuration',
        allowBlank : false
    } ],
    buttons : [ {
        text : 'Clear'
    }, {
        text : 'Save',
        action : 'save-conf'
    }, {
        text : 'Delete',
        action : 'del-conf'
    } ]
});