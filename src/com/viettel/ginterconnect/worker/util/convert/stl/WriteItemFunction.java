/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.stl;

/**
 *
 * @author chungdq
 */
public class WriteItemFunction extends WriteItem {

    int dynamicCount = 0;

    public BaseFunction function;

    private WriteItem columns[];

    public void setFunction(BaseFunction function) {
        this.function = function;
    }

    public void setColumns(WriteItem columns[]) {
        this.columns = columns;
        dynamicCount = 0;
        for (int i = 0; i < columns.length; i++) {
            if (this.columns[i].getType() != WriteItem.FIELD_TYPE_CONSTANT && this.columns[i].getType() != WriteItem.FIELD_TYPE_FUNCTION) {
                dynamicCount++;
            }
        }
    }

    public WriteItem[] getColumns() {
        return columns;
    }

    public WriteItemFunction() {
        super(WriteItem.FIELD_TYPE_FUNCTION);
    }

    public int getDynamicCount() {
        return dynamicCount;
    }

    @Override
    public void caculate(Object... args) throws Exception {
        value = null;
        int count_in = 0;
        boolean error = false;
        Object argus[] = new Object[columns.length];
        for (int i = 0; i < columns.length; i++) {
            switch (columns[i].getType()) {
                case WriteItem.FIELD_TYPE_CONSTANT:
                    argus[i] = columns[i].getValue();
                    break;
                case WriteItem.FIELD_TYPE_FIELD:
                    argus[i] = args[count_in];
                    count_in++;
                    break;
                case WriteItem.FIELD_TYPE_MAP:
                    columns[i].caculate(args[count_in]);
                    count_in++;
                    argus[i] = columns[i].getValue();
                default:
                    break;
            }
            if (count_in > args.length) {
                error = true;
                break;
            }
        }
        //calculate
        if (!error) {
            value = this.function.calculate(argus);
        }
    }

}
