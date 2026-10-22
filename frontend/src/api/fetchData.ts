

const fetchAllUsers = async () => {
      const response = await fetch('/users')
      if (!response.ok) {
        throw new Error('Network response was not ok')
      }
      
      const contentType = response.headers.get("content-type") || "";
      const isJson = contentType.includes("application/json");
      const body = isJson ? await response.json() : await response.text();

      if (!response.ok) {
          const errorMessage =
              (isJson && (body.message || body.error)) ||
              `HTTP ${response.status} ${response.statusText}`;
          throw new Error(errorMessage);
      }

      return body;
    }
    
    
export { fetchAllUsers }

