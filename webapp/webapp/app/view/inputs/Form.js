/**
 * Form to edit an input.
 * Uses a grid to configure the files for an input.
 */
Ext.define('eSalsa.view.inputs.Form', {
    extend : 'Ext.form.Panel',
    alias : 'widget.inputsform',
    defaultType : 'textfield',
    bodyPadding : 5,
    layout : 'anchor',
    disabled : true,
    uses : [ 'eSalsa.view.inputs.Files' ],
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
        fieldLabel : 'Files',
        xtype : 'inputfileslist',
        name : 'files'
    } ],
    buttons : [ {
        text : 'Clear'
    }, {
        text : 'Save',
        action: 'save-input'
    }, {
        text : 'Delete',
        action: 'del-input'
    } ]
});