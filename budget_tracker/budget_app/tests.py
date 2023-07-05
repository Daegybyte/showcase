from django.test import TestCase


# run tests with python manage.py test
# run tests with coverage with coverage run --source='.' manage.py test

# write an example test
class BudgetAppTests(TestCase):
    def test_home_page_status_code(self):
        response = self.client.get('/')
        self.assertEqual(response.status_code, 200)
    
    def test_about_page_status_code(self):
        response = self.client.get('/about/')
        self.assertEqual(response.status_code, 200)

    def test_page_not_found_status_code(self):
        response = self.client.get('/does_not_exist/')
        self.assertEqual(response.status_code, 404)