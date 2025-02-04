import axios from 'axios'

// 部门和岗位名称缓存
const departmentMap = new Map()
const positionMap = new Map()

// 获取部门列表并缓存
export const fetchDepartments = async () => {
  try {
    const response = await axios.get('/department/query')
    if (response.data.code === 200) {
      departmentMap.clear()
      response.data.data.forEach(dept => {
        departmentMap.set(dept.departmentId, dept.departmentName)
      })
      return response.data.data
    }
    return []
  } catch (error) {
    console.error('获取部门列表错误：', error)
    return []
  }
}

// 获取岗位列表并缓存
export const fetchPositions = async (departmentId) => {
  if (!departmentId) return []
  
  try {
    const response = await axios.get(`/position/query/${departmentId}`)
    if (response.data.code === 200) {
      if (!positionMap.has(departmentId)) {
        positionMap.set(departmentId, new Map())
      }
      response.data.data.forEach(pos => {
        positionMap.get(departmentId).set(pos.positionId, pos.positionName)
      })
      return response.data.data
    }
    return []
  } catch (error) {
    console.error('获取岗位列表错误：', error)
    return []
  }
}

// 获取部门名称
export const getDepartmentName = (departmentId) => {
  return departmentMap.get(departmentId) || `部门${departmentId}`
}

// 获取岗位名称
export const getPositionName = (departmentId, positionId) => {
  const deptPositions = positionMap.get(departmentId)
  if (deptPositions) {
    return deptPositions.get(positionId) || `岗位${positionId}`
  }
  return `岗位${positionId}`
} 