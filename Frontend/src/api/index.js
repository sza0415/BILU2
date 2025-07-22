/**
 * API模块统一导出
 */

import * as caseApi from './case'
import * as recordApi from './record'
import * as secrecyApi from './secrecy'
import * as authApi from './auth'

// 导出所有API
export {
  caseApi,
  recordApi,
  secrecyApi,
  authApi
}

// 默认导出所有API的集合
export default {
  case: caseApi,
  record: recordApi,
  secrecy: secrecyApi,
  auth: authApi
}