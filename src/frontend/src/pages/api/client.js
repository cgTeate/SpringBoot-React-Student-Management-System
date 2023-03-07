import axios from 'axios';
// import fetch from 'unfetch';
// const url = process.env.NEXT_PUBLIC_SPRINGBOOT_API_URL

//method to return all students
const checkStatus = response => {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }
  // convert non-2xx HTTP responses into errors:
  const error = new Error(response.statusText);
  error.response = response;
  throw error;
};

export const getAllStudents = () =>
  axios.get("http://localhost:8080/api/v1/students")
    .then(checkStatus);
