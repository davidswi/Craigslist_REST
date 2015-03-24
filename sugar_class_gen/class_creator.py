__author__ = 'dave'
import json

class SugarClassCreator(object):
    def __init__(self, json_for_remote_object):
        self.param_dict = json.loads(json_for_remote_object)

    def make_property_specs(self, prop_dict):
        property_specs = list()

        for key in prop_dict.keys():
            prop_name = key
            prop_value = prop_dict[key]
            type_of_prop = type(prop_value)
            if type_of_prop is dict:
                android_type = 'Class'
            elif type_of_prop is list:
                android_type = 'Array'
            elif type_of_prop is unicode:
                android_type = 'String'
            elif type_of_prop is int:
                android_type = 'int'
            elif type_of_prop is float:
                android_type = 'float'
            android_prop_def = {'name':prop_name, 'type':android_type}
            property_specs.append(android_prop_def)
        return property_specs

    def make_android_class_declaration(self, class_name):
        cased_class_name = class_name.title()
        new_android_class_text = 'public class ' + cased_class_name + ' extends SugarRecord<' + cased_class_name + '> {\n'
        return new_android_class_text

    def make_android_prop_declaration(self, android_prop_def, is_array=False):
        if is_array:
            prop_type = android_prop_def['type'] + '[]'
        else:
            prop_type = android_prop_def['type']
        prop_declaration = prop_type + ' ' + android_prop_def['name'].title() + ';'
        return prop_declaration

    def make_android_class_prop_declaration(self, android_class_name, is_array=False):
        if is_array:
            class_type = android_class_name + '[]'
        else:
            class_type = android_class_name
        class_prop_declaration = class_type.title() + ' ' + android_class_name.lower() + ';'
        return class_prop_declaration

    def make_android_class(self, param_dict, android_class_name=None):
        class_name = None
        curr_param_dict = param_dict
        next_param_dict = None

        if android_class_name is not None:
            android_class = self.make_android_class_declaration(android_class_name)
        else:
            android_class = None

        while curr_param_dict is not None:
            prop_specs = self.make_property_specs(curr_param_dict)
            for prop_spec in prop_specs:
                if prop_spec['type'] is 'Class':
                    if android_class is not None:
                        # nested class, recurse
                        nested_class_name = prop_spec['name']
                        nested_param_dict = curr_param_dict[nested_class_name]
                        nested_class = self.make_android_class(nested_param_dict, nested_class_name)
                        class_decl = self.make_android_class_prop_declaration(nested_class_name)
                        android_class += '\t' + class_decl + '\n'
                    else:
                        android_class_name = prop_spec['name']
                        android_class = self.make_android_class_declaration(android_class_name)
                        next_param_dict = curr_param_dict[android_class_name]
                elif prop_spec['type'] is 'Array':
                    # Process the first element as representative of all elements
                    next_param_dict = curr_param_dict[android_class_name][0]
                else:
                    android_prop_decl = self.make_android_prop_declaration(prop_spec)
                    android_class += '\t' + android_prop_decl + '\n'

            curr_param_dict = next_param_dict
            next_param_dict = None

        android_class += '}\n'

        # Save class file
        filename = android_class_name + '.java'
        h = open(filename, 'w')
        h.write(android_class)
        h.close()

        return android_class

    def make_android_classes(self):
        self.make_android_class(self.param_dict)