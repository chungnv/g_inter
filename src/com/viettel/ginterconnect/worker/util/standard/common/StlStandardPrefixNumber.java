/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.standard.common;

import com.viettel.ginterconnect.cached.bean.CommonDmsPrefix;
import com.viettel.ginterconnect.process.exception.StandardException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ubuntu
 */
public class StlStandardPrefixNumber {

    public static CommonDmsPrefix getDomesticPrefixNumber(String phoneNumber, int recordType, Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> mapPrefixNumber)
            throws StandardException {
        CommonDmsPrefix result = null;
        if (mapPrefixNumber == null) {
            return null;
        }
        if (mapPrefixNumber.containsKey("HIGH")) {
            Map<Long, Map<String, List<CommonDmsPrefix>>> mapPrecisePrefix = (Map) mapPrefixNumber.get("HIGH");
            result = getCodeTypePrefixNumber(phoneNumber, recordType, mapPrecisePrefix);
        }
        if ((result == null)
                && (mapPrefixNumber.containsKey("NORMAL"))) {
            Map<Long, Map<String, List<CommonDmsPrefix>>> mapPrefixPrefix = (Map) mapPrefixNumber.get("NORMAL");
            result = getCodeTypePrefixNumber(phoneNumber, recordType, mapPrefixPrefix);
        }

        if ((result == null)
                && (mapPrefixNumber.containsKey("LOW"))) {
            Map<Long, Map<String, List<CommonDmsPrefix>>> mapNormalPrefix = (Map) mapPrefixNumber.get("LOW");
            result = getCodeTypePrefixNumber(phoneNumber, recordType, mapNormalPrefix);
        }

        return result;
    }

    public static CommonDmsPrefix getCodeTypePrefixNumber(String phoneNumber, int recordType, Map<Long, Map<String, List<CommonDmsPrefix>>> mapPrefixNumber) throws StandardException {
        boolean isGotHead = false;
        CommonDmsPrefix fullHeadBO = null;
        if ((phoneNumber != null) && (phoneNumber.length() > 0)) {
            String standardNumber = phoneNumber;
//            if (!phoneNumber.startsWith("856")) {

//                if ((phoneNumber.startsWith("0")) && (!phoneNumber.startsWith("00"))) {
//                    standardNumber = phoneNumber.substring(1);
//                }
            int lengthNumber = standardNumber.length();

            for (int i = standardNumber.length(); i > 0; i--) {
                if (mapPrefixNumber.containsKey(new Long(i))) {
                    Map<String, List<CommonDmsPrefix>> mapLenPrefixNumber = (Map) mapPrefixNumber.get(new Long(i));

                    String key = standardNumber.substring(0, i);
                    if (mapLenPrefixNumber.containsKey(key)) {
                        List<CommonDmsPrefix> listHead = (List) mapLenPrefixNumber.get(key);
                        if (recordType == 1) {
                            if (listHead.size() == 1) {
                                CommonDmsPrefix headBO = (CommonDmsPrefix) listHead.get(0);

                                if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 2)) {
                                    if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
                                        if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) <= headBO.getLength().longValue()) {
                                            fullHeadBO = (CommonDmsPrefix) listHead.get(0);
                                            isGotHead = true;
                                            break;
                                        }
                                    }
                                }
                            } else if (listHead.size() > 1) {
                                for (CommonDmsPrefix headBO : listHead) {
                                    if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 2)) {
                                        if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
                                            if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) <= headBO.getLength().longValue()) {
                                                fullHeadBO = headBO;
                                                isGotHead = true;
                                                break;
                                            }
                                        } else {
                                            if ((headBO.getLength() != null) && (headBO.getLength().intValue() > 0)) {
                                                if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) >= headBO.getLength().longValue()) {
                                                    fullHeadBO = headBO;
                                                    isGotHead = true;
                                                    break;
                                                }
                                            }
                                            if ((headBO.getLength() == null) || (headBO.getLength().intValue() == 0)) {
                                                fullHeadBO = headBO;
                                                isGotHead = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (recordType == 2) {
                            if (listHead.size() == 1) {
                                CommonDmsPrefix headBO = (CommonDmsPrefix) listHead.get(0);
                                if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 1)) {
                                    if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
                                        if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) <= headBO.getLength().longValue()) {
                                            fullHeadBO = (CommonDmsPrefix) listHead.get(0);
                                            isGotHead = true;
                                            break;
                                        }
                                    } else {
                                        fullHeadBO = (CommonDmsPrefix) listHead.get(0);
                                        isGotHead = true;
                                        break;
                                    }
                                }
                            } else if (listHead.size() > 1) {
                                for (CommonDmsPrefix headBO : listHead) {
                                    if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 1)) {
                                        if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
                                            if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) <= headBO.getLength().longValue()) {
                                                fullHeadBO = (CommonDmsPrefix) listHead.get(0);
                                                isGotHead = true;
                                                break;
                                            }
                                        } else {
                                            if ((headBO.getLength() != null) && (headBO.getLength().intValue() > 0)) {
                                                if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) == headBO.getLength().longValue()) {
                                                    fullHeadBO = headBO;
                                                    isGotHead = true;
                                                    break;
                                                }
                                            }
                                            if ((headBO.getLength() == null) || (headBO.getLength().intValue() == 0)) {
                                                fullHeadBO = headBO;
                                                isGotHead = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (isGotHead) {
                    break;
                }

            }
//            } else {
//                standardNumber = phoneNumber.substring("856".length());
//                int lengthNumber = standardNumber.length();
//
//                for (int i = standardNumber.length(); i > 0; i--) {
//                    if (mapPrefixNumber.containsKey(new Long(i))) {
//                        Map<String, List<CommonDmsPrefix>> mapLenPrefixNumber = (Map) mapPrefixNumber.get(new Long(i));
//
//                        String key = standardNumber.substring(0, i);
//                        if (mapLenPrefixNumber.containsKey(key)) {
//                            List<CommonDmsPrefix> listHead = (List) mapLenPrefixNumber.get(key);
//                            if (recordType == 1) {
//                                if (listHead.size() == 1) {
//                                    CommonDmsPrefix headBO = (CommonDmsPrefix) listHead.get(0);
//                                    if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 2)) {
//                                        if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                            if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                            }
//                                        } else {
//                                            fullHeadBO = (CommonDmsPrefix) listHead.get(0);
//                                            isGotHead = true;
//                                            break;
//                                        }
//                                    }
//                                } else if (listHead.size() > 1) {
//                                    for (CommonDmsPrefix headBO : listHead) {
//                                        if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 2)) {
//                                            if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                                if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                                }
//                                            } else {
//                                                if ((headBO.getLength() != null) && (headBO.getLength().intValue() > 0)) {
//                                                    if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) >= headBO.getLength().longValue()) {
//                                                        fullHeadBO = headBO;
//                                                        isGotHead = true;
//                                                        break;
//                                                    }
//                                                }
//                                                if ((headBO.getLength() == null) || (headBO.getLength().intValue() == 0)) {
//                                                    fullHeadBO = headBO;
//                                                    isGotHead = true;
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            } else if (recordType == 2) {
//                                if (listHead.size() == 1) {
//                                    CommonDmsPrefix headBO = (CommonDmsPrefix) listHead.get(0);
//                                    if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 1)) {
//                                        if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                            if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                            }
//                                        } else {
//                                            fullHeadBO = (CommonDmsPrefix) listHead.get(0);
//                                            isGotHead = true;
//                                            break;
//                                        }
//                                    }
//                                } else if (listHead.size() > 1) {
//                                    for (CommonDmsPrefix headBO : listHead) {
//                                        if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 1)) {
//                                            if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                                if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                                }
//                                            } else {
//                                                if ((headBO.getLength() != null) && (headBO.getLength().intValue() > 0)) {
//                                                    if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) >= headBO.getLength().longValue()) {
//                                                        fullHeadBO = headBO;
//                                                        isGotHead = true;
//                                                        break;
//                                                    }
//                                                }
//                                                if ((headBO.getLength() == null) || (headBO.getLength().intValue() == 0)) {
//                                                    fullHeadBO = headBO;
//                                                    isGotHead = true;
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if (isGotHead) {
//                        break;
//                    }
//                }
//
//                if (!isGotHead) {
//                    standardNumber = new String(phoneNumber);
//                    lengthNumber = standardNumber.length();
//
//                    for (int i = standardNumber.length(); i > 0; i--) {
//                        if (mapPrefixNumber.containsKey(new Long(i))) {
//                            Map<String, List<CommonDmsPrefix>> mapLenPrefixNumber = (Map) mapPrefixNumber.get(new Long(i));
//
//                            String key = standardNumber.substring(0, i);
//                            if (mapLenPrefixNumber.containsKey(key)) {
//                                List<CommonDmsPrefix> listHead = (List) mapLenPrefixNumber.get(key);
//                                if (recordType == 1) {
//                                    if (listHead.size() == 1) {
//                                        CommonDmsPrefix headBO = (CommonDmsPrefix) listHead.get(0);
//                                        if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 2)) {
//                                            if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                                if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                                }
//                                            } else {
//                                                fullHeadBO = (CommonDmsPrefix) listHead.get(0);
//                                                isGotHead = true;
//                                                break;
//                                            }
//                                        }
//                                    } else if (listHead.size() > 1) {
//                                        for (CommonDmsPrefix headBO : listHead) {
//                                            if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 2)) {
//                                                if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                                    if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                                    }
//                                                } else {
//                                                    if ((headBO.getLength() != null) && (headBO.getLength().intValue() > 0)) {
//                                                        if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) == headBO.getLength().longValue()) {
//                                                            fullHeadBO = headBO;
//                                                            isGotHead = true;
//                                                            break;
//                                                        }
//                                                    }
//                                                    if ((headBO.getLength() == null) && (headBO.getLength().intValue() == 0)) {
//                                                        fullHeadBO = headBO;
//                                                        isGotHead = true;
//                                                        break;
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                } else if (recordType == 2) {
//                                    if (listHead.size() == 1) {
//                                        CommonDmsPrefix headBO = (CommonDmsPrefix) listHead.get(0);
//                                        if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 1)) {
//                                            if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                                if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                                }
//                                            } else {
//                                                fullHeadBO = (CommonDmsPrefix) listHead.get(0);
//                                                isGotHead = true;
//                                                break;
//                                            }
//                                        }
//                                    } else if (listHead.size() > 1) {
//                                        for (CommonDmsPrefix headBO : listHead) {
//                                            if ((headBO.getNumberFunction().intValue() == 3) || (headBO.getNumberFunction().intValue() == 1)) {
//                                                if ((headBO.getLength() != null) && (headBO.getLength().intValue() != 0)) {
//                                                    if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) < headBO.getLength().longValue()) {
//                                                    }
//                                                } else {
//                                                    if ((headBO.getLength() != null) && (headBO.getLength().intValue() > 0)) {
//                                                        if (lengthNumber + (headBO.getAddPrefix() == null ? 0 : headBO.getAddPrefix().length()) >= headBO.getLength().longValue()) {
//                                                            fullHeadBO = headBO;
//                                                            isGotHead = true;
//                                                            break;
//                                                        }
//                                                    }
//                                                    if ((headBO.getLength() == null) && (headBO.getLength().intValue() == 0)) {
//                                                        fullHeadBO = headBO;
//                                                        isGotHead = true;
//                                                        break;
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        if (isGotHead) {
//                            break;
//                        }
//                    }
//                }
//            }
        }

        return fullHeadBO;
    }

}
