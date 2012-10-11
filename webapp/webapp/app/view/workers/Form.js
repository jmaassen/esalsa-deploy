/**
 * Form to edit a worker.
 * Uses a property grid for configuring additional properties.
 */
Ext.define('eSalsa.view.workers.Form', {
    extend: 'Ext.form.Panel',
    alias: 'widget.workersform',
    requires: [
        'eSalsa.view.workers.AdditionalProperties'
    ],
    defaultType: 'textfield',
    bodyPadding: 5,
    layout: 'anchor',
    disabled: true,
    defaults: {
        anchor: '100%',
        labelAlign: 'top'
    },
    items: [{
        fieldLabel: 'ID',
        name: 'id',
        readOnly: true,
        allowBlank: false
    }, {
        fieldLabel: 'Comment',
        name: 'comment',
        emptyText: 'Enter comment',
        xtype: 'textarea'
    }, {
        fieldLabel: 'URI',
        name: 'uri',
        allowBlank: false
    }, {
        fieldLabel: 'Template directory',
        name : 'template_dir',
        allowBlank: false
    }, {
        fieldLabel: 'Input directory',
        name: 'input_dir',
        allowBlank: false
    }, {
        fieldLabel: 'Output directory',
        name: 'output_dir',
        allowBlank: false
    }, {
        title: 'Additional Properties',
        xtype: 'worker.additionalprops',
        name: 'add_props',
        nameColumnWidth: 250
    }],
    buttons: [{
        text: 'Clear'
    }, {
        text: 'Save',
        action: 'save-worker'
    }, {
        text: 'Delete',
        action: 'del-worker'
    }],
    /**
     * Shortcut to fetch form element for additional properties.
     *
     * @returns {eSalsa.view.workers.AdditionalProperties}
     */
    getAdditionalProperties: function() {
        return this.getForm().findField('add_props');
    }
});