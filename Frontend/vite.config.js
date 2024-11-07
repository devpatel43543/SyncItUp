import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'


// https://vitejs.dev/config/
export default defineConfig(
  ({mode}) => {
    let returnObject = {
      plugins: [react()],
      preview: {
        host: true,
        port: 80
      }
    };
    if(mode === 'prod'){
      return {
        ...returnObject,
        preview: {
          ...returnObject.preview,
          port: 81
        }
      }
    }
    return returnObject;
  }
)


