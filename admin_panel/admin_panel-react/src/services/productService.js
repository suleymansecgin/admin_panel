import axios from 'axios'

const API_BASE_URL = '/api/products'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Token'ı otomatik olarak header'a ekleyen interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export const productService = {
  async getAllProducts() {
    try {
      const response = await api.get('/')
      return response.data
    } catch (error) {
      if (error.response) {
        const errorData = error.response.data
        let errorMessage = 'Ürünler yüklenirken bir hata oluştu.'
        
        if (errorData?.message) {
          // Eğer message bir Map ise (validation hataları)
          if (typeof errorData.message === 'object' && !Array.isArray(errorData.message)) {
            const validationErrors = Object.values(errorData.message).join(', ')
            errorMessage = validationErrors || errorMessage
          }
          // Eğer message bir ErrorMessage objesi ise
          else if (errorData.message?.messageType?.message) {
            errorMessage = errorData.message.messageType.message
            if (errorData.message.ofStatic) {
              errorMessage += ': ' + errorData.message.ofStatic
            }
          }
          // Eğer message bir string ise
          else if (typeof errorData.message === 'string') {
            errorMessage = errorData.message
          }
        }
        
        throw new Error(errorMessage)
      }
      throw new Error('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.')
    }
  },

  async getProductById(id) {
    try {
      const response = await api.get(`/${id}`)
      return response.data
    } catch (error) {
      if (error.response) {
        const errorData = error.response.data
        let errorMessage = 'Ürün yüklenirken bir hata oluştu.'
        
        if (errorData?.message) {
          // Eğer message bir Map ise (validation hataları)
          if (typeof errorData.message === 'object' && !Array.isArray(errorData.message)) {
            const validationErrors = Object.values(errorData.message).join(', ')
            errorMessage = validationErrors || errorMessage
          }
          // Eğer message bir ErrorMessage objesi ise
          else if (errorData.message?.messageType?.message) {
            errorMessage = errorData.message.messageType.message
            if (errorData.message.ofStatic) {
              errorMessage += ': ' + errorData.message.ofStatic
            }
          }
          // Eğer message bir string ise
          else if (typeof errorData.message === 'string') {
            errorMessage = errorData.message
          }
        }
        
        throw new Error(errorMessage)
      }
      throw new Error('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.')
    }
  },

  async createProduct(productData) {
    try {
      console.log('Ürün ekleme isteği:', JSON.stringify(productData, null, 2))
      const response = await api.post('/', productData)
      console.log('Ürün ekleme başarılı:', response.data)
      return response.data
    } catch (error) {
      console.error('Ürün ekleme hatası detayı:', error)
      console.error('Error response:', error.response)
      console.error('Response status:', error.response?.status)
      console.error('Response data:', JSON.stringify(error.response?.data, null, 2))
      console.error('Request URL:', error.config?.url)
      console.error('Request method:', error.config?.method)
      
      if (error.response) {
        const errorData = error.response.data
        let errorMessage = 'Ürün eklenirken bir hata oluştu.'
        
        if (errorData?.message) {
          // Eğer message bir Map ise (validation hataları)
          if (typeof errorData.message === 'object' && !Array.isArray(errorData.message)) {
            const validationErrors = Object.values(errorData.message).join(', ')
            errorMessage = validationErrors || errorMessage
          }
          // Eğer message bir ErrorMessage objesi ise
          else if (errorData.message?.messageType?.message) {
            errorMessage = errorData.message.messageType.message
            if (errorData.message.ofStatic) {
              errorMessage += ': ' + errorData.message.ofStatic
            }
          }
          // Eğer message bir string ise
          else if (typeof errorData.message === 'string') {
            errorMessage = errorData.message
          }
        }
        
        throw new Error(errorMessage)
      }
      throw new Error('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.')
    }
  },

  async updateProduct(id, productData) {
    try {
      const response = await api.put(`/${id}`, productData)
      return response.data
    } catch (error) {
      if (error.response) {
        const errorData = error.response.data
        let errorMessage = 'Ürün güncellenirken bir hata oluştu.'
        
        if (errorData?.message) {
          // Eğer message bir Map ise (validation hataları)
          if (typeof errorData.message === 'object' && !Array.isArray(errorData.message)) {
            const validationErrors = Object.values(errorData.message).join(', ')
            errorMessage = validationErrors || errorMessage
          }
          // Eğer message bir ErrorMessage objesi ise
          else if (errorData.message?.messageType?.message) {
            errorMessage = errorData.message.messageType.message
            if (errorData.message.ofStatic) {
              errorMessage += ': ' + errorData.message.ofStatic
            }
          }
          // Eğer message bir string ise
          else if (typeof errorData.message === 'string') {
            errorMessage = errorData.message
          }
        }
        
        throw new Error(errorMessage)
      }
      throw new Error('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.')
    }
  },

  async deleteProduct(id) {
    try {
      await api.delete(`/${id}`)
    } catch (error) {
      if (error.response) {
        const errorData = error.response.data
        let errorMessage = 'Ürün silinirken bir hata oluştu.'
        
        if (errorData?.message) {
          // Eğer message bir Map ise (validation hataları)
          if (typeof errorData.message === 'object' && !Array.isArray(errorData.message)) {
            const validationErrors = Object.values(errorData.message).join(', ')
            errorMessage = validationErrors || errorMessage
          }
          // Eğer message bir ErrorMessage objesi ise
          else if (errorData.message?.messageType?.message) {
            errorMessage = errorData.message.messageType.message
            if (errorData.message.ofStatic) {
              errorMessage += ': ' + errorData.message.ofStatic
            }
          }
          // Eğer message bir string ise
          else if (typeof errorData.message === 'string') {
            errorMessage = errorData.message
          }
        }
        
        throw new Error(errorMessage)
      }
      throw new Error('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.')
    }
  },
}

export default productService

