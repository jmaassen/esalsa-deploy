/**
 * List of files.
 * Can be used a form field.
 */
Ext.define('eSalsa.view.inputs.Files', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.inputfileslist',
    title : 'Files',
    store : 'Files',
    mixins : [ 'Ext.form.field.Field' ],
    requires : [ 'Ext.Action', 'Ext.grid.plugin.CellEditing' ],
    allowDeselect : true,
    initComponent : function() {
        var me = this;
        this.addAction = Ext.create('Ext.Action', {
            scope : this,
            iconCls : 'icon-add',
            text : 'Add file',
            handler : this.onAddClick
        });
        this.removeAction = Ext.create('Ext.Action', {
            itemId : 'removeFile',
            scope : this,
            text : 'Remove file',
            iconCls : 'icon-del',
            disabled : true,
            handler : this.onDelClick
        // can only remove a selected property, so start disabled
        });

        this.columns = [ {
            text : 'File',
            dataIndex : 'file',
            flex : 1,
            editor : {
                allowBlank : false
            }
        } ];

        var editing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToMoveEditor : 1,
            autoCancel : false
        });

        Ext.apply(this, {
            plugins : [ editing ],
            listeners : {
                'selectionchange' : function(view, records) {
                    me.down('#removeFile').setDisabled(!records.length);
                }
            }
        });

        this.tbar = [ this.addAction, this.removeAction ];

        this.callParent(arguments);

        // init field mixin
        this.value = this.getValue();
        this.initField();
    },
    setValue : function(value) {
        // value must be converted to fit in store
        // ['file1', 'file2'] -> [['file1'], ['file2']]
        var nest = value.map(function(r) {
            return [ r ];
        });
        this.getStore().loadRawData(nest);
    },
    getValue : function() {
        var files = [];
        this.getStore().each(function(r) {
            files.push(r.data.file);
        });
        return files;
    },
    onAddClick : function() {
        var rec = Ext.create('eSalsa.model.File', {
            file : ''
        });
        this.getStore().insert(0, rec);
        this.plugins[0].startEditByPosition({
            row : 0,
            column : 0
        });
    },
    onDelClick : function() {
        var selection = this.getView().getSelectionModel().getSelection();
        this.getStore().remove(selection);
    }
});