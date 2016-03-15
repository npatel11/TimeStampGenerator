require 'calabash-android/calabash_steps'

When /^I touch the Add Timestamp button$/ do
 btn_txt = 'Timestamp'
 touch("button text:#{btn_txt}")
end

Then /^I see timestamp$/ do
  check_element_exists("tableLayout placeholder:'mytable'")
end
