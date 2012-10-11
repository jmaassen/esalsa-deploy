/**
 * Web interface to setup an eSalsa POP experiment.
 */
Ext.application({
    name : 'eSalsa',
    autoCreateViewport : true,
    controllers : [ 'Workers', 'Inputs', 'Configurations', 'Experiments' ]
});