/**
 * Editable list of experiments.
 */
Ext.define('eSalsa.view.experiments.List', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.experimentslist',
    store : 'Experiments',
    title : 'Experiments',
    uses : [ 'Ext.grid.column.Action', 'Ext.grid.plugin.RowEditing' ],
    allowDeselect : true,
    initComponent : function() {

        this.columns = [ {
            text : 'ID',
            dataIndex : 'id',
            editor : {
                xtype : 'textfield',
                allowBlank : false
            },
            flex : 1
        }, {
            text : 'Comment',
            dataIndex : 'comment',
            hidden : true,
            editor : {
                xtype : 'textarea'
            }
        }, {
            text : 'Worker',
            dataIndex : 'worker',
            editor : {
                xtype : 'combo',
                store : 'Workers',
                queryMode : 'local',
                displayField : 'id',
                valueField : 'id',
                forceSelection : true
            }
        }, {
            text : 'Configuration',
            dataIndex : 'configuration',
            editor : {
                xtype : 'combo',
                store : 'Configurations',
                queryMode : 'local',
                displayField : 'id',
                valueField : 'id',
                forceSelection : true
            }
        }, {
            text : 'Input',
            dataIndex : 'input',
            editor : {
                xtype : 'combo',
                store : 'Inputs',
                queryMode : 'local',
                displayField : 'id',
                valueField : 'id',
                forceSelection : true
            }
        }, {
            xtype : 'actioncolumn',
            width : 50,
            items : [ {
                getClass : function() {
                    return 'icon-del';
                },
                tooltip : 'Delete',
                scope : this,
                handler : function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
                    this.fireEvent('deleteExperiment', rec, rowIndex);
                }
            } ]
        } ];

        var editing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor : 1,
            autoCancel : false
        });

        Ext.apply(this, {
            plugins : [ editing ]
        });

        this.tbar = [ {
            text : 'Add experiment',
            iconCls : 'icon-add',
            handler : this.onAddClick,
            scope : this
        } ];

        /**
         * @event deleteExperiment
         * Fires when delete button is pressed
         * @param {eSalsa.model.Experiment} Experiment record
         * @param {Number} Row index
         */
        this.addEvents('deleteExperiment');

        this.callParent(arguments);

    },
    onAddClick : function() {
        var rec = Ext.create('eSalsa.model.Experiment');
        this.getStore().insert(0, rec);
        this.plugins[0].startEdit(rec, 0);
    }
});